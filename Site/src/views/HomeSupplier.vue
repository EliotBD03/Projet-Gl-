<template>
  <div class="home">
    <div class="title">
      <MainHeader text="header.home"/>
    </div>
    <div class="allcards">
      <MainCard :text="$t('maincard.clients')"/>
      <MainCard :text="$t('header.contracts')"/>
      <MainCard text="$t('header.notifications')"/>
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
    /*Méthode qui permet la déconnexion de l'utilisateur*/
    callDisconnect(){
      GlobalMethods.disconnect("/");
    }
  },
  mounted() {
    if (this.$cookies.get("lang")) {
      this.$i18n.locale = this.$cookies.get("lang");
    } else {
      this.$cookies.set("lang", this.$i18n.locale)
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