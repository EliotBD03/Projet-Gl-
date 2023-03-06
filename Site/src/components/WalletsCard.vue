<template>
  <div class="container">
    <div class="title">
      {{ name }}
      {{ owner }}
      {{ adress }}
    </div>
    <div class="button">
      <GoButton text="Go" :color="'green'" :redirect="redir" :expr="choose"/>
    </div>
  </div>
</template>
<script>
import GoButton from "@/components/GoButton.vue";
export default {
  name: "WalletsCard.vue",
  props: ["name", "owner", "adress", "redir", "choose"],
  components: {
    GoButton
  }
}
</script>

<style scoped>

.container {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: space-evenly;
  width: 400px;
  height: 500px;
  box-shadow: 0px 4px 4px rgba(0, 0, 0, 0.25);
  margin: 10px;
}

.title {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  margin: 50px;
}

.button {
  height: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
}
</style>