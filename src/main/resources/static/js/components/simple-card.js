export let comp0 = Vue.component('simple-card', {
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

export let comp1 = Vue.component('card-input', {
    template: 
    `<div>
        <template>
            <label class="card-label">{{label}}</label>
            <input :type="type" :name="name" :key="name" :value="value" class="card-input">
        </template>
    </div>`,
    props:['name', 'label', 'type', 'value']
});

export let comp2 = Vue.component('card-button', {
    template:
    `<div style="">
        <button :type="type" class="card-button">{{label}}</button>
    </div>`,
    props:['label', 'type']
});

export let comp3 = Vue.component('card-image', {
    template:
    `<div>
        <img :src="src" class="resized-image center-image">
    </div>`,
    props:['src']
});