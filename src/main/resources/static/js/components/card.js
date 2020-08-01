export default Vue.component('simple-card', {
    template:
    `<div class="simple-card">
        <slot></slot>
    </div>`,
    props:{
        card: { reqired: false }
    }
});