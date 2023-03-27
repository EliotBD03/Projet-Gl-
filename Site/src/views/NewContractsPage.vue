<template>
  <div class="main">
    <div class="header">
      <MainHeader text="header.newcontracts"/>
    </div>
    <div class="container">
      <div class="list">
      </div>
      <div class="homeButton" @click.prevent.left="$router.push('/Home')">
      <GoButton text="header.home" :colore="'#B1B9FC'"/>
        </div>
  </div>
  </div>
</template>
<script>
import MainHeader from "@/components/MainHeader.vue";
import GoButton from "@/components/GoButton.vue";
export default {
  components: {
    MainHeader,
    GoButton,
  },
  /*Méthode pour charger la langue sauvegardée en cookie*/
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

.main {
  display: flex;
  flex-direction: column;
  justify-content: space-evenly;
  height: 100vh;
}

.header {
  display: flex;
  align-items: center;
  justify-content: center;
  z-index: 9999; 
}


.container {
  display: flex;
  flex-direction: column;
  justify-content: space-evenly;
}

.list {
  display: flex;
  align-items: center;
  justify-content: center;
}
</style>