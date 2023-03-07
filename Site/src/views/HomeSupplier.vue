<template>
  <div class="home">
    <div class="title">
      <MainHeader text="Home"/>
    </div>
    <div class="allcards">
      <MainCard text="Your clients" redir="/wallets"/>
      <MainCard text="Your Contracts" redir="/contracts"/>
      <MainCard text="Notifications" redir="/notifications"/>
    </div>
    <div class="newcontract" @click.prevent.left="$router.push('/newcontracts')">
      <GoButton text="See new contracts" :colore="'#B1B9FC'"/>
    </div>
    <div class="bottombutton">
      <div class="disconnectbutton" @click.prevent.left="disconnect()">
      <GoButton text="Disconnect" :colore="'red'"/>
      </div>
      <div class="settingsbutton" @click.prevent.left="$router.push('/settings')">
      <GoButton text="Settings" :colore="'gray'"/>
        </div>
    </div>
  </div>
</template>

<script>
import MainCard from "@/components/MainCard.vue";
import GoButton from "@/components/GoButton.vue";
import MainHeader from "@/components/MainHeader.vue";
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
      GlobalMethods.disconnect("/");
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