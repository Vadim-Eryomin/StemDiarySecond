export let side = new Vue({
    el: '#side',
    methods: {
        profile: () => {
            fetch("profile")
            .then((data) => data.json())
            .then((data) => console.log(data.login));
        }
    },
});