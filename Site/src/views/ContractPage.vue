<template>
  <div class="main">
    <div class="header">
      <MainHeader text="Your contracts"/>
    </div>
    <div class="searchbar">
      <SearchBar/>
    </div>
    <div class="homeButton" @click.prevent.left="$router.push('/')">
      <GoButton text="Home" :colore="'#B1B9FC'"/>
    </div>
  </div>
</template>

<script>
import MainHeader from "@/components/MainHeader.vue";
import SearchBar from "@/components/SearchBar.vue";
import GoButton from "@/components/GoButton.vue";
export default {
  components: {
    MainHeader,
    SearchBar,
    GoButton,
  },
  props: {
    contract: [
      {
        name: "Dupuis",
        EAN: "1234567890123",
        date: "01/01/2020",
      },
      {
        name: "Bourgies",
        EAN: "2356789012345",
        date: "02/02/2020",
      },
      {
        name: "D'Haene",
        EAN: "3456789012345",
        date: "03/03/2020",
      }
    ]
  },
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

.homeButton {
  display: flex;
  justify-content: center;
}
</style>