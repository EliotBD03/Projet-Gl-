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
        <GoButton text="Disconnect"/>
        <GoButton text="Settings"/>
      </div>
    </div>
</template>

<script>
import MainCard from "@/components/MainCard.vue";
import GoButton from "@/components/GoButton.vue";
import MainHeader from "@/components/MainHeader.vue";
export default {
  components: {
    GoButton,
    MainCard,
    MainHeader
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