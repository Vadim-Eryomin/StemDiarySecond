export default Vue.component('card-button', {
    template:
    `
        <button :type="type" class="card-button">{{label}}</button>
    `,
    props:['label', 'type']
});