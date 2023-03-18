<template>
    <div class="container">
      <div class="title">
        {{ text }}
      </div>
      <div class="button" @click.prevent.left="$router.push({name: redir})">
        <GoButton text="button.go" :colore="'#B1B9FC'"/>
      </div>
    </div>
</template>
<script>
import GoButton from "@/components/GoButton.vue";
export default {
  name: "MainCard",
  props: ["text","redir","choose"],
  components: {
    GoButton,
  },

};
</script>

<style scoped>
.container {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  width: 400px;
  height: 300px;
  box-shadow: 0 15px 50px rgba(177, 185, 252, 1);
}

.title {
  background: #B1B9FC;
  width: 100%;
  height: 50%;

  display: flex;
  align-items: center;
  justify-content: center;

  font-weight: 1000;
  font-size: 40px;
  line-height: 48px;
  text-align: center;
}

.button {
  height: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
}
</style>