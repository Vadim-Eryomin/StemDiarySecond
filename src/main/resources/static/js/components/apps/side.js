export default new Vue({
    el: '#side',
    data: { isAdmin: false },
    methods: {
        shop: () => window.fetchPage('shop'),
        basket: () => window.fetchPage('basket'),
        timetable: () => window.fetchPage('timetable'),
        adminprofile: () => window.fetchPage('adminprofile'),
        adminbasket: () => window.fetchPage('adminbasket'),
        adminshop: () => window.fetchPage('adminshop'),
        admintimetable: () => window.fetchPage('admintimetable'),
        profile: () => {
            swipe(() => {
                fetch("auth", {
                    method: 'POST',
                    body: JSON.stringify({'login': window.content.login, 'password': window.content.password})
                })
                .then(data => data.json())
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
            })
        },
        admin: function(){
            swipe(() => {
                window.content.site = 'admin',
                this.isAdmin = true;
            })
        },
    },
});