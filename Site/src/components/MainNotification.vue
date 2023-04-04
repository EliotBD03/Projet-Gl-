<template>
    <div class="card">
        <div class="img"></div>
        <div class="textBox">
            <div class="textContent">
                <div class="h1">{{ $t("notifications.title",{name: title}) }}</div>
                <span class="span">{{ getElapsedTime() }}</span>
            </div>
            <div class="p">{{  $t("notifications.text",{text: texte}) }}</div>
            <div>
            </div>
        </div>
        <div class="statesButtons">
            <div class="interactbutton" v-if="checkStatus()">
                <div class="acceptbutton" @click.prevent.left="accept(id_notification)">
                    ACCEPT
                </div>
                <div class="refusebutton" @click.prevent.left="refuse(id_notification)">
                    REFUSE
                </div>
            </div>
            <div class="deletedbutton" v-else>
                <div class="seenbutton" @click.prevent.left="deleted(id_notification)">
                    SEEN
                </div>
            </div>
        </div>
    </div>
</template>

<script>
import moment from "moment";
export default {
    name: "MainNotification",
    props: ["time","text","color","id","id_notification"],
    data() {
        return {
            title: "",
            texte: "",
        }
    },
    methods: {
        getElapsedTime() {
            const time = moment(this.time);
            const now = moment();
            const elapsedTime = moment.duration(now.diff(time));
            const elapsedHours = elapsedTime.asHours();
            const elapsedMinutes = elapsedTime.asMinutes();
            if (elapsedHours >= 1) {
                return Math.floor(elapsedHours) + " hours ago";
            } else if (elapsedMinutes >= 1) {
                return Math.floor(elapsedMinutes) + " minutes ago";
            } else {
                return "Just now";
            }
        },
        updateTextAndTitle() {
            let title = "";
            let texte = this.text;
            if (this.text.includes("accepted by")) {
                title = this.text.split("accepted by")[1].trim();
                texte = "Has accepted your contract";
            } else if (this.text.includes("denied by")) {
                title = this.text.split("denied by")[1].trim();
                texte = "Has denied your contract";
            } else if (this.text.includes("from")) {
                title = this.text.split("from")[1].trim();
                texte = "Contract request";
            }
            this.title = title;
            this.texte = texte;
        },
        accept() {
            this.$emit("accept", this.id_notification);
        },
        refuse() {
            this.$emit("refuse", this.id_notification);
        },
        deleted() {
            this.$emit("delete", this.id_notification);
        },
        checkStatus() {
            console.log(this.text.includes("request"));
            return !!this.text.includes("request");
        }
    },
    mounted() {
        this.updateTextAndTitle();
    }
}
</script>

<style scoped>
.card {
    width: 700px;
    height: 70px;
    background: #353535;
    border-radius: 20px;
    display: flex;
    align-items: center;
    justify-content: left;
    backdrop-filter: blur(10px);
    transition: 0.5s ease-in-out;
}

.card:hover {
    cursor: pointer;
    transform: scale(1.05);
}

.img {
    width: 50px;
    height: 50px;
    margin-left: 10px;
    border-radius: 10px;
    background: linear-gradient(#d7cfcf, #9198e5);
}

.card:hover > .img {
    transition: 0.5s ease-in-out;
    background-repeat: no-repeat;
    background-position: center;
    background-size: 50px;
    background: linear-gradient(#3348f3, #9198e5);
}

.textBox {
    width: calc(100% - 90px);
    margin-left: 10px;
    color: white;
}

.textContent {
    display: flex;
    align-items: center;
    justify-content: space-between;
}

.span {
    font-size: 10px;
}

.h1 {
    font-size: 16px;
    font-weight: bold;
}

.p {
    font-size: 12px;
    font-weight: lighter;
    display: flex;
    align-items: center;
}

.statesButtons {
    display: flex;
    align-items: center;
    justify-content: center;
    flex-direction: column;
    margin: 20px;
}

.acceptbutton {
    width: 50px;
    height: 20px;
    background: #34c98e;
    border-radius: 10px;
    display: flex;
    align-items: center;
    justify-content: center;
    font-size: 10px;
    color: white;
    margin-bottom: 10px;
}

.refusebutton {
    width: 50px;
    height: 20px;
    background: red;
    border-radius: 10px;
    display: flex;
    align-items: center;
    justify-content: center;
    font-size: 10px;
    color: white;
}

.seenbutton {
    width: 50px;
    height: 20px;
    background: cornflowerblue;
    border-radius: 10px;
    display: flex;
    align-items: center;
    justify-content: center;
    font-size: 10px;
    color: white;
}
</style>