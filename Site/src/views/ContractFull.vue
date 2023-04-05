<template>
  <div class="main">
    <div class="header">
      <MainHeader :text="contract.name"/>
    </div>
    <div class ="list">
      <div v-if="isRole()">
        <p> Associated wallet : {{ associatedWallet }}</p>
        <p> Address : {{ addressWallet }}</p>
        <p> Ean : {{ contract.ean }}</p>
        <p> Provider : {{ contract.providerName }}</p>
      </div>
      <div v-else>
        <p> Client : {{ contract.nameClient }}</p>
        <p> Mail : {{ mailClient }}</p>
        <div @click.prevent.left="$router.push({ name: 'HomeSupplier' })"> <!--Adrien-->
          <GoButton text="Consumptions" :colore="'#34c98e'"/>
        </div>
      </div>
      <p> Type Of Energy : {{ contract.typeOfEnergy }}</p>
      <p> Location : {{ contract.location }}</p>
      <p> Price depends on the day : {{ contract.variableDayPrice }}</p>
      <p> Price depends on the night : {{ contract.variableNightPrice }}</p>
      <p> Start of peak-hours : {{ contract.startOfPeakHours }}</p>
      <p> End of peak-hours : {{ contract.endOfPeakHours }}</p>
      <p> Date of opening : {{ contract.openingDate }}</p>
      <p> Date of closure : {{ contract.closingDate }}</p>
      <p v-if="contract.isFixedRate">Fixed rate</p>
      <p v-else>Variable rate</p>
      <p v-if="contract.isSingleHourCounter">Single hour counter</p>
      <p v-else>No single hour counter</p>
    </div>
    <div class="bottombutton">
      <div class="backbutton" @click.prevent.left="back()">
      <GoButton text="Back" :colore="'red'"/>
      </div>
      <div class="closebutton" @click.prevent.left="deleteContract()">
      <GoButton text="Close the contract" :colore="'red'"/>
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
        idContract : sessionStorage.getItem('idContract'),
        contract : [],
        associatedWallet: '',
        addressWallet: '',
        mailClient: ''
      }},
    /**
    * Cette méthode récupère le contract pour lequel on veut plus d'informations à la création de la vue.
    * 
    * @throws une erreur potentiellement renvoyée par l'API ou une erreur de token gérée dans GlobalMethods.
    */
    async created(){
      const requestOptions = {
        method: "GET",
        headers: {'Authorization' : this.$cookies.get("token")}
      };
      try {
        const response = await fetch(`https://babawallet.alwaysdata.net/api/common/contracts/${this.idContract}`,requestOptions);
          if (!response.ok) { 
            if(response.status == 401){
              throw new Error("Token");
            }
            else{
              const data = await response.json();
              throw new Error(data.error);
            }
          } 
          else {
            const data = await response.json();
            this.contract = data.contract; 
            console.log(this.contract)
          }
      } catch(error) {
          if(error.message === "Token") {
            GlobalMethods.errorToken();
          } 
          else {
            GlobalMethods.errorApi(error.message);
          }
      }
    },
    methods: {
      /**
      * Cette méthode permet de supprimer un contrat.
      * 
      * @throws une erreur potentiellement renvoyée par l'API ou une erreur de token gérée dans GlobalMethods.
      */
      deleteContract() {
        const requestOptions = {
          method: "DELETE",
          headers: {'Authorization' : this.$cookies.get("token")}
        };
        fetch(`https://babawallet.alwaysdata.net/api/client/common/contracts/${this.idContract}`, requestOptions)
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
                  title: 'Good !',
                  text: 'Contract deleted !'
                })
                this.back();
              }
            })
            .catch(error => {
              if(error.message === "Token") {
                GlobalMethods.errorToken();
              } 
              else {
                GlobalMethods.errorApi(error.error);
              }
            });
      },
      /*Cette méthode permet de retourner à la page du clientFull en supprimant les informations du sessionStorage*/
      back(){
        sessionStorage.clear();
        if(this.isRole()){
            this.$router.push({name: 'Wallets'});
        }
        else{
            this.$router.push({name: 'Clients'});
        }
      },
      /*Méthode permettant de gérer le v-if en fonction de s'il s'agit d'un client ou non*/
      isRole(){
        if(this.$cookies.get("role") == "client"){
            this.associatedWallet = sessionStorage.getItem('walletName');
            this.addressWallet = sessionStorage.getItem('walletAddress');
            console.log(this.addressWallet)
            return true;
        }
        this.mailClient = sessionStorage.getItem('clientMail');
        return false;
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
  }
  
  .header {
    display: flex;
    align-items: center;
    justify-content: center;
    height: 15vh;
    z-index: 9999; 
  }
  
  .bottombutton {
    display: flex;
    flex-direction: row;
    align-items: center;
    justify-content: space-between;
    padding: 0 50px;
    margin-top: 50px;
    width: 100%;
  }
  
  .list{
    display: flex;
    border-bottom: 1px solid gray;
    align-items: center;
    flex-direction: column;
    width: 550px;
    height: 800px;
    border-radius: 50px;
    background: #e0e0e0;
    box-shadow: 0 15px 50px rgba(177, 185, 252, 1);
  }
  </style>
  