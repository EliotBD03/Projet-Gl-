<template>
    <div class="main">
        <div class="header">
            <MainHeader text="header.addwallet"/>
        </div>
        <div class="contact-form">
            <form id="addWallet" method="post" v-on:submit.prevent="post">
                <p>
                    <InputMain :text="$t('walletform.name')" v-model="name"/>
                </p>
                <p>
                    <InputMain :text="$t('walletform.address')" v-model="address"/>
                </p>
                <GoButton text="button.add" type="submit" :colore="'green'"/>
            </form>
        </div>
        <div class="backbutton" @click.prevent.left="$router.push('/wallets')">
            <GoButton text="button.back" :colore="'red'"/>
        </div>
    </div>
</template>

<script>
import GoButton from "@/components/GoButton.vue";
import GlobalMethods from "@/components/GlobalMethods.vue";
import Swal from 'sweetalert2';
import InputMain from "@/components/InputMain.vue";
import MainHeader from "@/components/MainHeader.vue";
export default {
    components: {InputMain, GoButton, MainHeader},
    data(){
        return{
            name: '',
            address: ''
        }},
    created() {
        GlobalMethods.getCurrentLanguage();
    },
    /*Méthode qui vérifie si les champs nom et adresse sont bien remplis sinon envoie une pop-up*/
    methods: {
        checkArgs(){
            if(!this.name) Swal.fire(this.$t("alerts.name"));
            else if(!this.address) Swal.fire(this.$t("alerts.address"));
            else return true;
        },
        /**
         * Cette méthode envoie le nom et l'adresse du portefeuille vers l'api afin de le créer si checkArgs() est true.
         *
         * @throws une erreur potentiellement renvoyée par l'API ou une erreur de token gérée dans GlobalMethods.
         */
        post(){
            if(this.checkArgs())
            {
                const requestOptions = {
                    method: "POST",
                    headers: {'Authorization': this.$cookies.get("token")},
                    body: JSON.stringify({ name: this.name, address: this.address })
                };
                fetch("https://babawallet.alwaysdata.net/api/client/wallets", requestOptions)
                    .then(response => {
                        if(!response.ok){
                            return response.json().then(json => Promise.reject(json));
                        }
                        else{
                            Swal.fire({
                                icon: 'success',
                                title: this.$t("alerts.good"),
                                text: this.$t("alerts.walletcreated")
                            })
                            this.$router.push({ name: 'Wallets' });
                        }
                    })
                    .catch(error => {
                        if(error.error === "error.unauthorizedAccess")
                            GlobalMethods.errorToken();
                        else {
                            GlobalMethods.errorApi(error.error);
                        }
                    });
            }
        }
    }
}
</script>

<style scoped>
.header {
    display: flex;
    align-items: center;
    justify-content: center;
    z-index: 9999;
}

.main {
    display: flex;
    flex-direction: column;
    justify-content: center;
    align-items: center;
    height: 100vh;
}
.contact-form {
    display: flex;
    align-items: center;
    justify-content: center;
    background-color: #f2f2f2;
    padding: 50px;
    border-radius: 10px;
    box-shadow: 0 15px 50px rgba(177, 185, 252, 1);
}

</style>
