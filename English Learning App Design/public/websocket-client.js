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