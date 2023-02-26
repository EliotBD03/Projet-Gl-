<template>
  <div class="main">
    <div class="header">
      <MainHeader text="Wallets"/>
    </div>
    <div class="allcards">
      <WalletsCard name="Nom 1" owner="Owner 1" adress="Adress 1"/>
      <WalletsCard name="Nom 2" owner="Owner 2" adress="Adress 2"/>
      <AddWalletForm/>
    </div>
    <div class="homebutton">
      <GoButton text="Home" redirect="/"/>
    </div>
  </div>
</template>
<script>
import MainHeader from "@/components/MainHeader.vue";
import WalletsCard from "@/components/WalletsCard.vue"
import GoButton from "@/components/GoButton.vue";
import AddWalletForm from "@/components/AddWalletForm.vue";
export default {
  components : {
    GoButton,
    WalletsCard,
    MainHeader,
    AddWalletForm
  }
}
</script>

<style scoped>

.header {
  display: flex;
  align-items: center;
  justify-content: center;
}

.main {
  display: flex;
  flex-direction: column;
  justify-content: space-evenly;
  height: 100vh;
}

.homebutton {
  display: flex;
  justify-content: center;
}

.allcards {
  display: flex;
  align-items: center;
  flex-direction: row;
  justify-content: space-evenly;
}
</style>