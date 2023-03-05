<template>
    <div class="home">
      <div class="title">
        <MainHeader text="Home"/>
      </div>
      <div class="allcards">
        <MainCard text="Wallet" redir="/wallets"/>
        <MainCard text="Your Contracts" redir="/contracts"/>
        <MainCard text="Notifications"/>
      </div>
      <div class="newcontract">
        <GoButton text="See new contracts"/>
      </div>
      <div class="bottombutton">
        <GoButton text="Disconnect" v-on:click="disconnect()"/>
        <GoButton text="Settings"/>
      </div>
    </div>
</template>

<script>
import MainCard from "@/components/MainCard.vue";
import GoButton from "@/components/GoButton.vue";
import MainHeader from "@/components/MainHeader.vue";
import Swal from 'sweetalert2';
import GlobalMethods from "@/components/GlobalMethods.vue";
export default {
  components: {
    GoButton,
    MainCard,
    MainHeader
  }, 
  methods: {
    /*Méthode qui permet la déconnexion de l'utilisateur*/
    disconnect(){
      const requestOptions = {
        method: "POST",
        headers: this.$cookies.get("token")
      };
      fetch("https://babawallet.alwaysdata.net:8300/api/disconnect", requestOptions)
        .then(response => {
            if(!response.ok){
              if(response.status == 401){
                this.$cookies.remove("token");
                Swal.fire('Your connection has expired');
                window.location.href = "/Login.vue";
              }
              else{
                GlobalMethods.methods.errorApi(response.status);
                throw new Error(response.status);
              }
            }
            else{
              this.$cookies.remove("token");
              Swal.fire('See you soon!');
              window.location.href = "/Login.vue";
            }
        }) 
        .catch(error => {
          console.error("Error", error);
        });
    }
  }
};
</script>

<style scoped>
.allcards {
  display: flex;
  flex-direction: row;
  align-items: center;
  justify-content: space-evenly;
}

.newcontract {
  display: flex;
  align-items: center;
  justify-content: center;
  margin-top: 50px;
}

.bottombutton {
  display: flex;
  flex-direction: row;
  align-items: center;
  justify-content: space-between;
  padding: 0 50px;
  margin-top: 50px;
}

.title {
  display: flex;
  align-items: center;
  justify-content: center;
}

.home {
  display: flex;
  flex-direction: column;
  justify-content: space-evenly;
  height: 100vh;
}
</style>