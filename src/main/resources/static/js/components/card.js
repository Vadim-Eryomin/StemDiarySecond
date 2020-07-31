export default Vue.component('simple-card', {
    template:
    `<div>
        <template>
            <template v-if="card!==undefined">
                <div class="grid-card">
                    <div v-if="card.head!==undefined" class="card-head">
                        {{card.head}}
                    </div>
                    <div v-if="card.image!==undefined" class="card-image">
                        <img :src="card.image" class="resized-image">
                    </div>
                    <div v-if="card.text!==undefined" class="card-text">
                        {{card.text}}
                    </div>
                    <div v-if="card.foot!==undefined" class="card-foot">
                        <button type="submit">
                            {{card.foot}}
                        </button>
                    </div>
                </div>
            </template>
            <template v-else>
                <div class="simple-card">
                    <slot></slot>
                </div>
            </template>
        </template>
    </div>`,
    props:{
        card: { reqired: false }
    }
});