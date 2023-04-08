<template>
    <div class="main">
        <div class="number">{{ $t("404.title") }}</div>
        <div class="text"><span>{{ $t("404.text1") }}</span><br>{{ $t("404.text2") }}</div>
        <a class="me" href="https://codepen.io/uzcho_/pens/popular/?grid_type=list" target="_blank"></a>
        <div class="backbutton" @click.prevent.left="back">
            <GoButton text="button.returnhome" :colore="'darkblue'"/>
            </div>
    </div>
</template>

<script>
import GlobalMethods from "@/components/GlobalMethods.vue";
import GoButton from "@/components/GoButton.vue";
export default {
    name: "NotFound",
    components: {
        GoButton
    },
    created() {
        GlobalMethods.getCurrentLanguage();
    },
    methods: {
        back() {
            sessionStorage.clear();
            GlobalMethods.isAClient();
        }
    }
}
</script>

<style scoped>
body {
    display: flex;
    flex-flow: row wrap;
    align-content: center;
    justify-content: center;
}

div {
    width: 100%;
    text-align: center;
}

.number {
    background: #fff;
    position: relative;
    font: 900 30vmin "Consolas";
    letter-spacing: 5vmin;
    text-shadow: 2px -1px 0 #000, 4px -2px 0 #0a0a0a, 6px -3px 0 #0f0f0f, 8px -4px 0 #141414, 10px -5px 0 #1a1a1a, 12px -6px 0 #1f1f1f, 14px -7px 0 #242424, 16px -8px 0 #292929;
}
.number::before {
    background-color: #B1B9FC;
    background-image: radial-gradient(closest-side at 50% 50%, #34c98e 100%, rgba(0, 0, 0, 0)), radial-gradient(closest-side at 50% 50%, #fc3f3f 100%, rgba(0, 0, 0, 0));
    background-repeat: repeat-x;
    background-size: 40vmin 40vmin;
    background-position: -100vmin 20vmin, 100vmin -25vmin;
    width: 100%;
    height: 100%;
    mix-blend-mode: screen;
    -webkit-animation: moving 10s linear infinite both;
    animation: moving 10s linear infinite both;
    display: block;
    position: absolute;
    content: "";
}
@-webkit-keyframes moving {
    to {
        background-position: 100vmin 20vmin, -100vmin -25vmin;
    }
}
@keyframes moving {
    to {
        background-position: 100vmin 20vmin, -100vmin -25vmin;
    }
}

.text {
    font: 400 5vmin "Courgette";
}
.text span {
    font-size: 10vmin;
}

.main {
    display: flex;
    justify-content: center;
    flex-direction: column;
    align-items: center;
}

.backbutton {
    margin-top: 20px;
}
</style>