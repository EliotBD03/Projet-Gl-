<template>
  <div class="main">
    <div class="header">
      <MainHeader text="Nom 1 (Wallet)"/>
    </div>
    <div class="bottombutton">
      <GoButton text="Back"/>
      <GoButton text="Consumptions"/>
      <GoButton text="Close the wallet"/>
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
}

.bottombutton {
  display: flex;
  flex-direction: row;
  align-items: center;
  justify-content: space-between;
  padding: 0 50px;
  margin-top: 50px;
}
</style>