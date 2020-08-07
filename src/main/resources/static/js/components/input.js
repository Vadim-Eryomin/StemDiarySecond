export default Vue.component('card-input', {
    template:
    `<div>
        <template>
            <label class="card-label">{{label}}</label>
            <input :type="type" :name="name" :key="name" :value="value" :checked="checked" class="card-input">
        </template>
    </div>`,
    props:['name', 'label', 'type', 'value', 'checked']
});