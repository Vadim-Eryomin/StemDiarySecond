export default Vue.component('card-button', {
    template:
    `<div>
        <button :type="type" class="card-button">{{label}}</button>
    </div>`,
    props:['label', 'type']
});