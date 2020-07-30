export let app = new Vue({
    el: '#content',
    data: {
        auth: false,
        site: ''
    },
    methods: {
        form: (method, formId, fields) => {
            //get form by id
            let form = document.getElementById(formId);
            let obj = {};
            //get all need data
            for(let item of fields) obj[item] = form[item].value;
            //send and get answer
            (async function(){
                await fetch(method, {method: 'POST', body: JSON.stringify(obj)})
                .then(data => data.json())
                .then(data => window.data = data);
                //you can use it as window.data
            })();
        }
    }
})