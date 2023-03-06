<template>
    <div class="home">
      <div class="title">
        <MainHeader text="Home"/>
      </div>
      <div class="allcards">
        <MainCard text="Wallet" redir="/wallets" choose="change"/>
        <MainCard text="Your Contracts" redir="/contracts" choose="change"/>
        <MainCard text="Notifications" redir="/notifications" choose="change"/>
      </div>
      <div class="newcontract">
        <GoButton text="See new contracts" :color="'#B1B9FC'" redirect="/newcontracts" expr="change"/>
      </div>
      <div class="bottombutton">
        <GoButton text="Disconnect" :color="'red'" expr="test"/>
        <GoButton text="Settings" :color="'gray'" redirect="/settings" expr="change"/>
      </div>
    </div>
</template>

<script>
import MainCard from "@/components/MainCard.vue";
import GoButton from "@/components/GoButton.vue";
import MainHeader from "@/components/MainHeader.vue";
import GlobalMethods from "@/components/GlobalMethods.vue";
import Swal from 'sweetalert2';
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