<template>
  <div class="main">
    <div class="header">
      <MainHeader text="Your contracts"/>
    </div>
    <div class="searchbar">
      <SearchBar/>
    </div>
  </div>
</template>

<script>
import MainHeader from "@/components/MainHeader.vue";
import SearchBar from "@/components/SearchBar.vue";
export default {
  components: {
    MainHeader,
    SearchBar
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

.searchbar {
  display: flex;
  align-items: center;
  justify-content: center;
}
</style>