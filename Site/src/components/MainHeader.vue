<template>
  <div class="title">
    {{ text }}
  </div>
</template>

<script>
export default {
  name: "MainHeader",
  props: ["text"]
}
</script>

<style scoped>
.title{
  position: absolute;
  width: 100%;
  height: 75px;
  left: 0;
  top: 0;
  font-weight: 1000;
  font-size: 50px;
  background-color: rgba(177, 185, 252, 1);
  display: flex;
  align-items: center;
  justify-content: center;
}
</style>