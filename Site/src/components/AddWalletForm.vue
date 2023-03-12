<template>
  <!--Voir si cela ne cause pas pb pour actualiser après ajout-->
  <div class="contact-form">
    <form id="addWallet" method="post" v-on:submit.prevent="post">
      <p>
        <InputMain :text="$t('walletform.name')" v-model="name"/>
      </p>
      <p>
        <InputMain :text="$t('walletform.adress')" v-model="address"/>
      </p>
      <GoButton text="button.add" type="submit" :colore="'gray'"/>
      <!-- <button class = "greenButton rightButton fixed" type="submit"> ADD </button>-->
    </form>
  </div>
</template>

<script>
import GoButton from "@/components/GoButton.vue";
import GlobalMethods from "@/components/GlobalMethods.vue";
import Swal from 'sweetalert2';
import InputMain from "@/components/InputMain.vue";
export default {
  name: "ContactForm",
  components: {InputMain, GoButton},
  data(){
    return{
      name: '',
      address: ''
    }},
  /*Méthode qui vérifie si les champs sont bien remplis sinon envoie un pop-up*/
  methods: {
    checkArgs(){
      if(!this.name) Swal.fire("Please enter your name");
      if(!this.address) Swal.fire("Please enter your address");
      else return true;
    },
    /*Méthode qui, si checkArgs() est true, envoie le nom et l'adresse du portefeuille vers l'api 
    pour le créer*/
    post(){
      if(this.checkArgs())
      {
        const requestOptions = {
          method: "POST",
          headers: {'Authorization': this.$cookies.get("token")},
          body: JSON.stringify({ name: this.name, address: this.address })
        };
        fetch("http://services-babawallet.alwaysdata.net:8300/api/client/wallets", requestOptions)
            .then(response => {
              if(!response.ok){
                if(response.status == 401){
                  this.$cookies.remove("role");
                  this.$cookies.remove("token");
                  Swal.fire('Your connection has expired');
                  this.$router.push("/");
                  throw new Error(response.status);
                }
                else{
                  const data = response.json();
                  GlobalMethods.errorApi(data.error);
                  throw new Error(data.error);
                }
              }
            })
            .catch(error => {
              console.error(error);
            });
        this.$router.push("/wallets");
      }
    }
  }
}
</script>

<style scoped>
.contact-form {
  display: flex;
  align-items: center;
  justify-content: center;
  background-color: #f2f2f2;
  padding: 50px;
  border-radius: 10px;
}

</style>