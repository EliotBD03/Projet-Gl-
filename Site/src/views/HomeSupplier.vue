<template>
  <div class="home">
    <div class="title">
      <MainHeader text="header.home"/>
    </div>
    <div class="allcards">
      <MainCard :text="$t('maincard.clients')" redir="Clients"/>
      <MainCard :text="$t('header.contracts')" redir="ContractsSupplier"/>
      <MainCard :text="$t('header.notifications')" redir="Notifications"/>
    </div>
    <div class="bottombutton">
      <div class="disconnectbutton" @click.prevent.left="callDisconnect()">
      <GoButton text="button.disconnect" :colore="'red'"/>
      </div>
      <div class="settingsbutton" @click.prevent.left="$router.push('/settings')">
      <GoButton text="header.settings" :colore="'gray'"/>
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
    /*Méthode qui permet la déconnexion de l'utilisateur à l'aide de GlobalMethods*/
    callDisconnect(){
      GlobalMethods.disconnect("/");
    }
  },
  /*Méthode pour charger la langue sauvegardée en cookie*/
  created() {
    GlobalMethods.getCurrentLanguage();
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