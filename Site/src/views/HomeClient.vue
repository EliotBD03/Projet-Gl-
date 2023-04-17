<template>
    <div class="home">
      <div class="title">
        <MainHeader text="header.home"/>
      </div>
      <div class="allcards">
        <MainCard :text="$t('header.wallets')" redir="Wallets"/>
        <MainCard :text="$t('header.contracts')" redir="Contracts"/>
        <MainCard :text="$t('header.notifications')" redir="Notifications"/>
      </div>
      <div class="globalcontracts"> <!-- div for the button to avoid the 100% width on clicking -->
        <div class="middle" @click.prevent.left="$router.push('/newcontracts')">
          <GoButton text="maincard.newcontracts" :colore="'#B1B9FC'"/>
        </div>
        <div class="middle" @click.prevent.left="$router.push({ name: 'InvitedWallets' })">
        <GoButton text="GestionExtClaire.wallets" :colore="'#B1B9FC'"/>
        </div>
        <div class="middle" @click.prevent.left="$router.push({ name: 'Invitations' })">
        <GoButton text="GestionExtClaire.invitation" :colore="'#B1B9FC'"/>
        </div>
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
      callDisconnect() {
          GlobalMethods.disconnect("/");
      },
  },
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

.middle {
  width: fit-content;
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

.globalcontracts {
  display: flex;
  align-items: center;
  justify-content: center;
  margin-top: 50px;
}


</style>