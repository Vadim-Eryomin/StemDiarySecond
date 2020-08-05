export default new Vue({
    el: '#side',
    methods: {
        profile: () => {
            fetch("auth", {
                method: 'POST',
                body: JSON.stringify({'login': window.content.login, 'password': window.content.password})
            })
            .then((data) => data.json())
            .then(data => {
                window.content.id = data.id;
                window.content.login = data.login;
                window.content.password = data.password;
                window.content.img = data.img;
                window.content.name = data.name;
                window.content.surname = data.surname;
                window.content.isAuth = true;
                window.content.coins = data.coins;
            })
            .then(data => window.content.site = 'profile')
            .then((data) => console.log(data.login));
        },
        shop: () => {
            fetch("shop", {
                method: 'POST',
                body: JSON.stringify({'login': window.content.login, 'password': window.content.password})
            })
            .then(data => data.json())
            .then(data => window.content.data = data)
            .then(data => window.content.site = 'shop');
        },
        basket: () => {
            fetch("basket", {
                method: 'POST',
                body: JSON.stringify({'login': window.content.login, 'password': window.content.password, 'id': window.content.id})
            })
            .then(data => data.json())
            .then(data => window.content.data = data)
            .then(data => window.content.site = 'basket');
        },
        timetable: () => {
            fetch("timetable", {
                method: 'POST',
                body: JSON.stringify({'login': window.content.login, 'password': window.content.password, 'id': window.content.id})
            })
            .then(data => data.json())
            .then(data => window.content.data = data)
            .then(data => window.content.site = 'timetable');
        }
    },
});