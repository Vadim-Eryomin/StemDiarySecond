export default new Vue({
    el: '#content',
    data: {
        isAuth: false,
        site: '',
        login: '',
        password: '',
        img: '',
        name: '',
        surname: '',
        id: 0,
        data: {},
        coins: 0,
        pupils: {},
        statuses: ['didnt read', 'in work', 'get this!', 'issued'],
        teachers: {},
        timetable: {},
        datestring: "",
        timestrimg: ""
    },
    methods: {
        auth: function(){
            fetchPage('auth', 'loginForm', null, () => {
            //можно оптимизировать, создав объект
                if(!data.auth) return;
                this.id = data.id;
                this.login = data.login;
                this.password = loginForm['password'].value;
                this.img = data.img;
                this.name = data.name;
                this.surname = data.surname;
                this.isAuth = true;
                document.cookie = "login="+(data.login);
                document.cookie = "password="+(data.password);
            })
        },
        buy: (id, cost) => fetchPage('buy', null, {'coins': content.coins, 'buy': id, 'cost': cost}, () => setTimeout(window.side.shop, 100)),
        confirm: (id) => fetchPage('confirm', null, {'confirm': id}, () => setTimeout(window.side.basket, 100)),
        decline: (id) => fetchPage('decline', null, {'decline': id}, () => setTimeout(window.side.basket, 100)),

        lesson: function(course){
            fetch("lesson", {
                method: 'POST',
                body: JSON.stringify({'login': window.content.login, 'password': window.content.password, 'id': window.content.id, 'course': course})
            })
            .then(data => data.json())
            .then(data => this.data = data)
            .then(data => this.site = 'lesson');
        },
        mark: function(course, lesson){
            fetch("mark", {
                method: 'POST',
                body: JSON.stringify({'login': window.content.login, 'password': window.content.password, 'id': window.content.id, 'course': course, 'lesson': lesson})
            })
            .then(data => data.json())
            .then(data => this.data = data)
            .then(data => this.site = 'mark')
            .then(data => console.log(this));
        },
        setmark: (id, lesson, pupil) => {
            let form = document.getElementById(id);
            fetch("setmark", {
                method: 'POST',
                body: JSON.stringify(
                    {'login': window.content.login,
                     'password': window.content.password,
                     'id': window.content.id,
                     'lesson': lesson,
                     'a': form['a'].value,
                     'b': form['b'].value,
                     'c': form['c'].value,
                     'pupil': pupil
               })
            })
            .then(window.side.timetable());
        },
        homework: function(course, lesson){
            console.log(course + '  ' + lesson);
            fetch("homework", {
                method: 'POST',
                body: JSON.stringify({'login': window.content.login, 'password': window.content.password, 'id': window.content.id, 'lesson': lesson, 'course': course})
            })
            .then(data => data.json())
            .then(data => this.data = data)
            .then(data => this.site = 'homework')
            .then(data => console.log(this));
        },
        sethomework: (id, lesson, course) => {
            let form = document.getElementById(id);
            fetch("sethomework", {
                method: 'POST',
                body: JSON.stringify(
                    {
                    'login': window.content.login,
                    'password': window.content.password,
                    'id': window.content.id,
                    'lesson': lesson,
                    'course': course,
                    'homework': form['homework'].value
               })
            })
            .then(window.side.timetable());
        },

        editprofile: function(id){
            this.data = this.data.find((item) => item.id === id);
            this.site = 'adminprofileedit';
        },
        saveprofile: function(id, formid){
            let form = document.getElementById(formid);
            fetch("saveprofile", {
                method: 'POST',
                body: JSON.stringify({
                'login': window.content.login,
                'password': window.content.password,
                'id': window.content.id,
                'saveid': id,
                'savelogin': form['login'].value,
                'savepassword': form['password'].value,
                'saveimg': form['img'].value,
                'savename': form['name'].value,
                'savesurname': form['surname'].value,
                'savecoins': form['coins'].value,
                'saveadmin': form['admin'].checked,
                'saveteacher': form['teacher'].checked,
                })
            })
            .then(data => window.side.adminprofile());
        },
        adminprofile: () => { window.side.adminprofile() },
        createprofile: function(){ this.editprofile(-1) },
        editbasket: function(id){
            this.data = this.data.find((item) => item.id === id);
            this.site = 'adminbasketedit';
        },
        savebasket: (id, formId) => fetchPage('savebasket', formId, null, () => window.side.adminbasket),
        saveshop: (id, formId) => fetchPage('saveshop', formId, null, () => {
            side.adminshop();
        }),
        createshop: function(){ this.editshop(-1) },
        edittimetable: function(id){
            this.timetable = this.data.find((item) => item.id === id);
            fetchPage('teachers', null, null, () => {
                content.teachers = content.data;
                content.site = 'admintimetableedit';
                let date = new Date(parseInt(content.timetable.coursefirstdate));
                content.datestring = date.getFullYear() + "-" + ((date.getMonth() + 1) < 10 ? "0" : "") + (date.getMonth() + 1) + "-" + (date.getDate() < 10 ? "0" : "") + date.getDate();
                content.timestring = (date.getHours() + new Date().getTimezoneOffset() / 60 < 10 ? "0" : "") + (date.getHours() + new Date().getTimezoneOffset() / 60) + ":" + (date.getMinutes() < 10 ? "0" : "") + date.getMinutes();
            })
        },
        savetimetable: function(id, formId){
            let form = document.getElementById(formId);
            let startObject = {'savedate': (form['date'].valueAsNumber + form['time'].valueAsNumber)};
            window.fetchPage('savetimetable', formId, startObject, window.side.admintimetable);
        },
        editshop: function(id){
            this.data = this.data.find((item) => item.id === id);
            this.site = 'adminshopedit';
        },
        adminshop: () => {window.side.adminshop()}
    }
})