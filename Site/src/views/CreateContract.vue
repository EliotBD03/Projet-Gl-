<template>
  <div class="main">
    <div class="header">
      <MainHeader text="Add a wallet"/>
    </div>
    <div class="contact-form">
      <form id="addWallet" method="post" v-on:submit.prevent="post">
        <p>
          <InputMain :text="$t('walletform.name')" v-model="name"/>
        </p>
        <p>
          <InputMain :text="'Type of energy'" v-model="typeofenergy"/>
        </p>
        <p>
          <label>
            <input type="checkbox" v-model="wallonie">
            Wallonie
          </label>
          <label>
            <input type="checkbox" v-model="flandre">
            Flandre
          </label>
          <label>
            <input type="checkbox" v-model="bruxelles">
            Bruxelles-Capitale
          </label>
        </p>
        <p>
          <InputMain :text="'Basic price'" v-model="basicprice"/>
        </p>
        <p>
          <InputMain :text="'Night price'" v-model="nightprice"/>
        </p>
        <p>
          <InputMain :text="'Day price'" v-model="dayprice"/>
        </p>
        <p>
          <InputMain :text="'Off-peak price'" v-model="offpeakprice"/>
        </p>
        <GoButton text="button.add" type="submit" :colore="'green'"/>
      </form>
    </div>
    <div class="backbutton" @click.prevent.left="$router.push('/wallets')">
      <GoButton text="Back" :colore="'darkblue'"/>
    </div>
  </div>
</template>

<script>
import GoButton from "@/components/GoButton.vue";
import Swal from 'sweetalert2';
import InputMain from "@/components/InputMain.vue";
import MainHeader from "@/components/MainHeader.vue";

export default {
  components: {InputMain, GoButton, MainHeader},
  data(){
    return{
      name: '',
      typeofenergy: '',
      wallonie: false,
      flandre: false,
      bruxelles: false,
      location: '000',
      basicprice: '',
      nightprice: '',
      dayprice: '',
      offpeakprice: ''
    }},
  watch: {
    wallonie() {
      this.updateLocation()
    },
    flandre() {
      this.updateLocation()
    },
    bruxelles() {
      this.updateLocation()
    }
  },
  /*Méthode qui vérifie si les champs sont bien remplis sinon envoie une pop-up*/
  methods: {
    updateLocation() {
      let location = ''
      if (this.wallonie) {
        location += '1'
      } else {
        location += '0'
      }
      if (this.flandre) {
        location += '1'
      } else {
        location += '0'
      }
      if (this.bruxelles) {
        location += '1'
      } else {
        location += '0'
      }
      this.location = location
    },
    checkArgs() {
      if (!this.name) Swal.fire("Please enter your name");
      else if (!this.typeofenergy) Swal.fire("Please enter the type of energy");
      else if (this.location === '000') Swal.fire("Please select at least one location");
      else if (!this.basicprice) Swal.fire("Please enter the basic price");
      else if (!this.nightprice) Swal.fire("Please enter the night price");
      else if (!this.dayprice) Swal.fire("Please enter the day price");
      else if (!this.offpeakprice) Swal.fire("Please enter the off-peak price");
      else return true;
    },
    post(){
      if (this.checkArgs()) {
        const requestOptions = {
          method: "POST",
          body: JSON.stringify({
            name: this.name,
            typeofenergy: this.typeofenergy,
            location: this.location,
            basicprice: this.basicprice,
            nightprice: this.nightprice,
            dayprice: this.dayprice,
            offpeakprice: this.offpeakprice
          }),
        };
        fetch("https://babawallet.alwaysdata.net/api/provider/propose_contract", requestOptions)
            .then(response => {
              if (!response.ok) {
                return response.json().then(json => Promise.reject(json));
              }
              return response.json();
            })
            .then(
                this.$router.push({name: 'HomeSupplier'}))
            .then(
              Swal.fire({
                icon: 'success',
                title: this.$t('alert.good'),
                text: 'Your contract has been added',
              })
            )
        }
      }
    }
}
</script>

<style scoped>

.main {
  display: flex;
  flex-direction: column;
  justify-content: space-evenly;
  align-items: center;
  height: 100vh;
}

.header {
  display: flex;
  align-items: center;
  justify-content: center;
  z-index: 9999; 
}

.contact-form{
  display: flex;
  align-items: center;
  justify-content: center;
  flex-direction: column;
  width: 600px;
  height: 700px;
  border-radius: 50px;
  background: #e0e0e0;
  box-shadow: 0 15px 50px rgba(177, 185, 252, 1);
}

</style>