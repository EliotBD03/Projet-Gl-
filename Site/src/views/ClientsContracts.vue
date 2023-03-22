<template>
  <div class="main">
    <div class="header">
      <MainHeader :text="contract.name"/>
    </div>
    <div class ="list">
      <p> General information : </p>
      <p> Client : {{ contract.nameClient }}</p>
      <p> Type of energy : {{ contract.typeOfEnergy }}</p>
      <p> Location : {{ contract.location }}</p>
      <p> Basic price : {{ contract.basicPrice }}</p>
      <p> Price depends on the day : {{ contract.variableDayPrice }}</p>
      <p> Price depends on the night : {{ contract.variableNightPrice }}</p>
      <p v-if="contract.fixedRate"> Fixed rate : Yes</p>
      <p v-else>Variable rate : Yes</p>
      <p> Start of off-peak hours : {{ contract.startOffOutPeakHours }}</p>
      <p> End of off-peak hours : {{ contract.endOffOutPeakHours }}</p>
      <p> Date of opening : {{ contract.openingDate }}</p>
      <p> Date of closure : {{ contract.closingDate }}</p>
    </div>
    <div class="bottombutton">
      <div class="backbutton" @click.prevent.left="back()">
        <GoButton text="Back" :colore="'red'"/>
      </div>
      <div class="closebutton" @click.prevent.left="deleteContract()">
        <GoButton text="Delete this contract" :colore="'red'"/>
      </div>
    </div>
  </div>
</template>
  <script>
  import GoButton from "@/components/GoButton.vue";
  import MainHeader from "@/components/MainHeader.vue";
  export default {
    components: {
      GoButton,
      MainHeader
    },
    data(){
      return{
        contract : sessionStorage.getItem('contract'),
      }},
   /*Méthode pour charger la langue sauvegardée en cookie*/
    mounted() {
      if (this.$cookies.get("lang")) {
        this.$i18n.locale = this.$cookies.get("lang");
      } else {
        this.$cookies.set("lang", this.$i18n.locale)
      }
    },
    methods: {
      /*Retourner à la page du clientFull en supprimant le contract du sessionStorage*/
      back(){
        sessionStorage.removeItem('contract');
        this.$router.push({name: 'ClientFull'});
      },
      deleteContract(){
        /*Comment fait-on la distinction entre un contrat général sans client 
        et celui d'un client spécifique pour le supprimer*/
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
  