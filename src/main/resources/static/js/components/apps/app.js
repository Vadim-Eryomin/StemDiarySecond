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

        lesson: (course) => fetchPage('lesson', null, {course}, null),
        mark: (course, lesson) => fetchPage('mark', null, {course, lesson}, null),
        setmark: (id, lesson, pupil) => fetchPage('setmark', id, {lesson, pupil}, () => setTimeout(window.side.timetable, 100)),
        homework: (course, lesson) => fetchPage('homework', null, {course, lesson}, null),
        sethomework: (id, lesson, course) => fetchPage('sethomework', id, {lesson, course}, () => setTimeout(window.side.timetable, 100)),

        editprofile: function(id){
            this.data = this.data.find((item) => item.id === id);
            this.site = 'adminprofileedit';
        },
        editbasket: function(id){
            this.data = this.data.find((item) => item.id === id);
            this.site = 'adminbasketedit';
        },
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
        editshop: function(id){
            this.data = this.data.find((item) => item.id === id);
            this.site = 'adminshopedit';
        },

        saveprofile: (id, formId) => fetchPage('saveprofile', formId, {'saveid': id, 'saveadmin': document.getElementById(formId)['saveadmin'].checked,
            'saveteacher': document.getElementById(formId)['saveteacher'].checked}, () => setTimeout(window.side.adminprofile, 100)),
        savebasket: (id, formId) => fetchPage('savebasket', formId, null, () => setTimeout(window.side.adminbasket, 100)),
        saveshop: (id, formId) => fetchPage('saveshop', formId, null, () => setTimeout(window.side.adminshop, 100)),
        savetimetable: (id, formId) => fetchPage('savetimetable', formId, {'savedate': (document.getElementById(formId)['date'].valueAsNumber
            + document.getElementById(formId)['time'].valueAsNumber)}, () => setTimeout(window.side.admintimetable, 100)),

        adminprofile: () => window.side.adminprofile(),
        adminshop: () => window.side.adminshop(),

        createprofile: function(){ this.editprofile(-1) },
        createshop: function(){ this.editshop(-1) },
        createtimetable: function(){ this.edittimetable(-1) },
    }
})