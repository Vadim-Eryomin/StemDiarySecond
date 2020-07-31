export default Vue.component('card-image', {
    template:
    `<div>
        <img :src="src" class="resized-image center-image">
    </div>`,
    props:['src']
});