<template>
    <div class="card">
        <div class="img"></div>
        <div class="textBox">
            <div class="textContent">
                <div class="h1">{{ $t("notifications.title",{name: title}) }}</div>
                <span class="span">{{ getElapsedTime() }}</span>
            </div>
            <div class="p">{{  $t("notifications.text",{text: texte}) }}</div>
            <div class="fields">
                <input type="text" v-model="ean" v-if="checkRole()" :placeholder="$t('client.eancode')">
                <input type="text" v-model="adress" v-if="checkRole()" :placeholder="$t('proposal.address')">
            </div>
            <div>
            </div>
        </div>
        <div class="statesButtons">
            <div class="interactbutton" v-if="checkStatus()">
                <div class="acceptbutton" @click.prevent.left="accept(id_notification)">
                    {{ $t("button.accept") }}
                </div>
                <div class="refusebutton" @click.prevent.left="refuse(id_notification)">
                    {{ $t("button.deny") }}
                </div>
                <div class="seebutton" @click.prevent.left="seeProposal(proposalName)">
                    {{ $t("button.infos") }}
                </div>
            </div>
            <div class="deletedbutton" v-else>
                <div class="seenbutton" @click.prevent.left="deleted(id_notification)">
                    {{ $t("button.seen") }}
                </div>
            </div>
        </div>
    </div>
</template>

<script>
import moment from "moment";
export default {
    name: "MainNotification",
    props: ["time","text","color","id_notification","proposalName", "providerId","role"],
    data() {
        return {
            title: "",
            texte: "",
            ean: "",
            adress: ""
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
                return this.$t("notifications.time", [{time: Math.floor(elapsedHours), format: this.$t("settings.hours")}]);
            } else if (elapsedMinutes >= 1) {
                return this.$t("notifications.time", {time: (Math.floor(elapsedMinutes)), format: this.$t("settings.minutes")});
            } else {
                return this.$t("settings.now");
            }
        },
        updateTextAndTitle() {
            let title = "";
            let texte = this.text;
            if (this.text.includes("accepted by")) {
                title = this.text.split("accepted by")[1].trim();
                texte = this.$t("notifications.accepted");
            } else if (this.text.includes("denied by")) {
                title = this.text.split("denied by")[1].trim();
                texte = this.$t("notifications.denied");
            } else if (this.text.includes("from")) {
                title = this.text.split("from")[1].trim();
                texte = this.$t("notifications.request");
            } else if (this.text.includes("daily consumption")) {
                const regex = /in the (.+) has changed to (.+) for this ean code : (.+)/;
                const match = regex.exec(this.text);
                const date = moment(match[1]).format("YYYY-MM-DD");
                const consumption = match[2];
                const ean = match[3];
                title = this.$t("notifications.system");
                texte = this.$t("notifications.dailychange", {date: date, consumption: consumption, ean: ean});
            } else if (this.text.includes("deleted by")) {
                title = this.text.split(":")[1].trim();
                texte = this.$t("notifications.deleted");
            } else if (this.text.includes("expired")) {
                title = this.$t("notifications.system");
                texte = this.$t("notifications.expired");
            }
            this.title = title;
            this.texte = texte;
        },
        accept() {
            this.$emit("accept", this.id_notification, this.ean, this.adress);
        },
        refuse() {
            this.$emit("refuse", this.id_notification);
        },
        deleted() {
            this.$emit("delete", this.id_notification);
        },
        seeProposal() {
            this.$emit("seeProposal", this.providerId, this.proposalName);
        },
        checkStatus() {
            return this.text.includes("request");
        },
        checkRole() {
            if (this.role === 'client') {
                return this.checkStatus();
            }
            else return false;
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
    height: 120px;
    background: #353535;
    border-radius: 20px;
    display: flex;
    align-items: center;
    justify-content: center;
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

.seebutton {
    width: 50px;
    height: 20px;
    background: cornflowerblue;
    border-radius: 10px;
    display: flex;
    align-items: center;
    justify-content: center;
    font-size: 10px;
    color: white;
    margin: 5px;
}

.interactbutton {
    display: flex;
    align-items: center;
    justify-content: center;
    flex-direction: column;
}

.fields {
    display: flex;
    align-items: center;
    justify-content: space-evenly;
    margin: 5px;
}
</style>