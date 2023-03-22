<template>
    <div class="main">
      <div class="header">
        <MainHeader :text="client.name"/>
      </div>
      <div class ="list">
        <p> General information : </p>
        <p> Mail : {{ client.mail }}</p>
        <p> Associated contracts :</p>
        <div v-if="client.contracts">
          <div v-for="contract in client.contracts" :key="contract.id">
            <p> name = {{ contract.name }}</p>
            <p> ean = {{ contract.ean }}</p>
            <p> Type Of Energie = {{ contract.typeOfEnergie }}</p>
            <!-- On prend bien la dernière conso sur les contrats ?-->
            <p> Last consumptions :</p>
            <p v-if="!contract.lastConsumptionOfWater">Water : No information</p>
            <p v-else>Water : {{ contract.lastConsumptionOfWater }}</p>
            <p v-if="!contract.lastConsumptionOfGas">Gas : No information</p>
            <p v-else>Gas : {{ contract.lastConsumptionOfGas }}</p>
            <p v-if="!contract.lastConsumptionOfElectricity">Electricity : No information</p>
            <p v-else>Electricity : {{ contract.lastConsumptionOfElectricity }}</p>
            <p>--------------------------</p>
            <!--Voir pour goButton contracts-->
          </div>
        </div>
        <div v-else> No information</div>
      </div>
      <div class="bottombutton">
        <div class="backbutton" @click.prevent.left="back()">
        <GoButton text="Back" :colore="'red'"/>
        </div>
        <div class="closebutton" @click.prevent.left="deleteClient()">
        <GoButton text="Delete client" :colore="'red'"/>
        </div>
      </div>
    </div>
  
  </template>
  <script>
  import GoButton from "@/components/GoButton.vue";
  import MainHeader from "@/components/MainHeader.vue";
  import Swal from 'sweetalert2';
  import GlobalMethods from "@/components/GlobalMethods.vue";
  export default {
    components: {
      GoButton,
      MainHeader
    },
    data(){
      return{
        client : sessionStorage.getItem('client'),
      }},
   /* /*Méthode pour charger la langue sauvegardée en cookie*/
    mounted() {
      if (this.$cookies.get("lang")) {
        this.$i18n.locale = this.$cookies.get("lang");
      } else {
        this.$cookies.set("lang", this.$i18n.locale)
      }
    },
    methods: {
      /* Méthode permettant de supprimer un client*/
      deleteClient() {
        const requestOptions = {
          method: "DELETE",
          headers: {'Authorization' : this.$cookies.get("token")}
        };
        fetch(`https://babawallet.alwaysdata.net/api/clients/clients_of_provider/${this.client.id_client}`, requestOptions)
            .then(response => {
              if(!response.ok){
                const data = response.text();
                if(response.status == 401 && data.trim() === ''){
                    throw new Error("Token");
                }
                else{
                  return response.json().then(json => Promise.reject(json));
                }
              }
              else{
                Swal.fire({
                  icon: 'success',
                  title: 'Good !',
                  text: 'Client deleted !'
                })
                this.$router.push({name: 'Clients'});
              }
            })
            .catch(error => {
              if(error.message === "Token") {
                this.$cookies.remove("token");
                this.$cookies.remove("role");
                Swal.fire('Your connection has expired');
                this.$router.push("/");
              } 
              else {
                GlobalMethods.errorApi(error.error);
              }
            });
      },
      /*Retourner à la page des clients en supprimant l'adresse du sessionStorage*/
      back(){
        sessionStorage.removeItem('client');
        this.$router.push({name: 'Clients'});
      }
    }
  };
  </script>
  
  <style scoped>
  
  .main {
    display: flex;
    flex-direction: column;
    justify-content: center;
    align-items: center;
    height: 100vh;
  }
  
  .header {
    display: flex;
    align-items: center;
    justify-content: center;
  }
  
  .bottombutton {
    display: flex;
    flex-direction: row;
    align-items: center;
    justify-content: space-between;
    padding: 0 50px;
    margin-top: 50px;
  }
  
  .list{
    display: flex;
      align-items: center;
      justify-content: center;
      flex-direction: column;
      width: 500px;
      height: 500px;
      border-radius: 50px;
      background: #e0e0e0;
      box-shadow: 0 15px 50px rgba(177, 185, 252, 1);
  }
  </style>
  