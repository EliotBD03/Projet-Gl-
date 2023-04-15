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
                    <InputMain :text="$t('walletform.adress')" v-model="address"/>
                </p>
                <!--adrien-->
                <div>
                    <p>
                        {{ $t('walletform.numberOfResidents') }}
                        <input class="inputSmall" v-model="numberOfResidents" type="number" min="1"/>
                    </p>
                </div>
                <div>
                    <p>
                        {{ $t('walletform.sizeOfHouse') }}
                        <input class="inputSmall" v-model="sizeOfHouse" type="number" min="1"/>
                    </p>
                </div>
                <div style="display: flex; flex-direction: column;">
                    <div style="display: inline;">
                        <p  style="display: inline-block; margin-right: 10px;">{{ $t('walletform.answer1') }}</p>
                        <input type="radio" id="typeOfHouseChoice1" name="typeOfHouse" value="house" checked>
                        <label for="typeOfHouseChoice1">{{ $t('walletform.typeHouse1') }}</label>

                        <input type="radio" id="typeOfHouseChoice2" value="apartment">
                        <label for="typeOfHouseChoice2">{{ $t('walletform.typeHouse2') }}</label>
                    </div>
                    <div style="display: inline;">
                        <p  style="display: inline-block; margin-right: 10px;">{{ $t('walletform.answer2') }}</p>
                        <input type="radio" id="typeOfChargeChoice1" name="typeOfCharge" value="electricity" checked>
                        <label for="typeOfChargeChoice1">{{ $t('walletform.typeCharge1') }}</label>

                        <input type="radio" id="typeOfChargeChoice2" value="gas">
                        <label for="typeOfChargeChoice2">{{ $t('walletform.typeCharge2') }}</label>
                    </div>
                    <div style="display: inline;">
                        <p  style="display: inline-block; margin-right: 10px;">{{ $t('walletform.answer3') }}</p>
                        <input type="radio" id="solarPanelChoice1" name="solarPanel" value="solarPanel" checked>
                        <label for="solarPanelChoice1">{{ $t('walletform.solarPanel1') }}</label>

                        <input type="radio" id="solarPanelChoice2" value="noSolarPanel">
                        <label for="solarPanelChoice2">{{ $t('walletform.solarPanel2') }}</label>
                    </div>
                </div>
                <!--adrien-->
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
            address: '',
            //adrien
            numberOfResidents: 1,
            sizeOfHouse: 1,
            house: true,
            flat: false
            //adrien
        }},
    created() {
        GlobalMethods.getCurrentLanguage();
    },
    /*Méthode qui vérifie si les champs nom et adresse sont bien remplis sinon envoie une pop-up*/
    methods: {
        checkArgs(){
            if(!this.name) Swal.fire(this.$t("alerts.name"));
            else if(!this.address) Swal.fire(this.$t("alerts.address"));
            //adrien
            else if(!this.numberOfResidents || this.numberOfResidents > 20) Swal.fire(this.$t("alerts.numberOfResidents"));
            else if(!this.sizeOfHouse || this.sizeOfHouse > 10000) Swal.fire(this.$t("alerts.sizeOfHouse"));
            //adrien
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
                //adrien
                let typeOfHouse = false;
                let typeOfCharge = false;
                let solarPanel = false;
                
                document.querySelector('input[name="typeOfHouse"]:checked').value == "house" ? typeOfHouse = true : typeOfHouse = false;
                document.querySelector('input[name="typeOfCharge"]:checked').value == "house" ? typeOfCharge = true : typeOfCharge = false;
                document.querySelector('input[name="solarPanel"]:checked').value == "house" ? solarPanel = true : solarPanel = false;
                //adrien

                const requestOptions = {
                    method: "POST",
                    headers: {'Authorization': this.$cookies.get("token")},
                    //adrien
                    body: JSON.stringify({ name: this.name, address: this.address, number_of_residents: this.numberOfResidents, size_of_house: this.sizeOfHouse, is_house: typeOfHouse, is_electricity_to_charge: typeOfCharge, solar_panels: solarPanel})
                };
                fetch("https://babawallet.alwaysdata.net/api/client/wallets", requestOptions)
                    .then(response => {
                        if(!response.ok){
                            if(response.status == 401){
                                throw new Error("Token");
                            }
                            else{
                                return response.json().then(json => Promise.reject(json));
                            }
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
                        if (error.message === "Token") {
                            GlobalMethods.errorToken();
                        }
                        else {
                            GlobalMethods.errorApi(error.error);
                        }
                    });
            }
        },
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
    display: inline-flex;
    align-items: center;
    justify-content: center;
    background-color: #f2f2f2;
    padding: 50px;
    border-radius: 10px;
    box-shadow: 0 15px 50px rgba(177, 185, 252, 1);
}

/*adrien*/
.contact-form p {
  white-space: nowrap;
}

.contact-form p * {
  display: inline-block;
  vertical-align: middle;
}

.inputSmall {
  font-weight: 500;
  font-size: 14px;
  height: 50px;
  width: 100px;
  border-radius: 10px;
  padding: 0 24px;
  border: none;
  border-bottom: 1px solid #e5e5e5;
  outline: none;
  display: flex;
  margin: 10px;
}

.inputSmall:focus {
  border-bottom: 1px solid #B1B9FC;
  -webkit-transition: 0.1s;
  transition: 0.5s;
}
/*adrien*/
</style>
