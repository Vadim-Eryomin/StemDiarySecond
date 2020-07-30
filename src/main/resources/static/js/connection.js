(async function(){
    console.log('Hello!');
    let socket = new SockJS('/stem-diary-api');
    window.stomp = Stomp.over(socket);
    window.stomp.connect({}, () => {
        window.stomp.subscribe('/api/status', (res) => checkAndSwipe(res))
    });
})();