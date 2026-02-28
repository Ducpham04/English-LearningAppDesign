// Kết nối WebSocket tới server qua SockJS
const socket = new SockJS('/ws');
const stompClient = Stomp.over(socket);

// Kết nối WebSocket
stompClient.connect({}, (frame) => {
    console.log('Connected: ' + frame);

    // Đăng ký topic để nhận tin nhắn từ server
    stompClient.subscribe('/topic/messages', (message) => {
        console.log('Message Received from Server: ', message.body);
    });
});

// Gửi tin nhắn từ client tới server
function sendMessage(messageContent) {
    stompClient.send('/app/send-message', {}, messageContent);
}

// Example: Gửi tin nhắn từ client
// sendMessage("Hello Server!");


//Xử lý chỉnh sửa nội dung tin nhắn trước khi gửi (trường hợp gửi metadata hoặc cấu trúc phức tạp)
function sendMessage(messageContent) {
    const payload = JSON.stringify({
        content: messageContent,
        timestamp: new Date().toISOString()
    });
    stompClient.send('/app/send-message', {}, payload); // Gửi định dạng JSON
}

//Lưu danh sách tin nhắn hoặc xử lý phản hồi server:
stompClient.subscribe('/topic/messages', (message) => {
    const parsedMessage = JSON.parse(message.body);
    console.log('Message content: ', parsedMessage.content);
    console.log('Timestamp: ', parsedMessage.timestamp);
});