package com.example.appenglish.modules.transcribe.service.transcript;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.services.transcribe.TranscribeClient;
import software.amazon.awssdk.services.transcribe.model.GetTranscriptionJobRequest;
import software.amazon.awssdk.services.transcribe.model.GetTranscriptionJobResponse;
import software.amazon.awssdk.services.transcribe.model.Media;
import software.amazon.awssdk.services.transcribe.model.StartTranscriptionJobRequest;
import software.amazon.awssdk.services.transcribe.model.TranscriptionJob;
import software.amazon.awssdk.services.transcribe.model.TranscriptionJobStatus;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class TranscriptionService {

    TranscribeClient transcribeClient;

    public void startJob(String jobName, String s3Uri, String languageCode) {
        StartTranscriptionJobRequest transcriptionJobRequest = StartTranscriptionJobRequest.builder()
                .transcriptionJobName(jobName)
                .languageCode(languageCode)
                .media(Media.builder().mediaFileUri(s3Uri).build())
                .build();
        transcribeClient.startTranscriptionJob(transcriptionJobRequest);
    }

    public String fetchTranscript(String jobName) throws Exception {
        TranscriptionJob job;
        do {
            GetTranscriptionJobResponse transcriptionJobResponse = transcribeClient.getTranscriptionJob(
                    GetTranscriptionJobRequest.builder().transcriptionJobName(jobName).build());
            job = transcriptionJobResponse.transcriptionJob();
            if (job.transcriptionJobStatus() == TranscriptionJobStatus.IN_PROGRESS) {
                Thread.sleep(2000);
            } else if (job.transcriptionJobStatus() == TranscriptionJobStatus.FAILED) {
                throw new RuntimeException("Transcription failed: " + job.failureReason());
            }
        } while (job.transcriptionJobStatus() == TranscriptionJobStatus.IN_PROGRESS);

        String transcriptUrl = job.transcript().transcriptFileUri();
        return HttpClient.newHttpClient()
                .send(HttpRequest.newBuilder(URI.create(transcriptUrl)).GET().build(), HttpResponse.BodyHandlers.ofString())
                .body();
    }
}

