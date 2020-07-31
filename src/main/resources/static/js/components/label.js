export default Vue.component('card-label', {
    template:
    `<div>
        <template>
            <label class="card-label">{{label}}</label>
        </template>
    </div>`,
    props:['label']
});