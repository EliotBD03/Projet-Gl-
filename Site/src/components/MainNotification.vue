<template>
  <div class="card">
    <div class="img"></div>
    <div class="textBox">
      <div class="textContent">
        <div class="h1">{{ title }}</div>
        <span class="span">{{ time }} minutes ago</span>
      </div>
      <div class="p">{{  text }}</div>
      <div>
      </div>
    </div>
  </div>
</template>

<script>
export default {
  name: "MainNotification",
  props: ["title","time","text","color"],
}
</script>

<style scoped>
.card {
  width: 500px;
  height: 70px;
  background: #353535;
  border-radius: 20px;
  display: flex;
  align-items: center;
  justify-content: left;
  backdrop-filter: blur(10px);
  transition: 0.5s ease-in-out;
}

.card:hover {
  cursor: pointer;
  transform: scale(1.05);
}

.img {
  width: 50px;
  height: 50px;
  margin-left: 10px;
  border-radius: 10px;
  background: linear-gradient(#d7cfcf, #9198e5);
}

.card:hover > .img {
  transition: 0.5s ease-in-out;
  background-image: url("@/assets/check.png");
  background-repeat: no-repeat;
  background-position: center;
  background-size: 50px;
  background: linear-gradient(#3348f3, #9198e5);
}

.textBox {
  width: calc(100% - 90px);
  margin-left: 10px;
  color: white;
}

.textContent {
  display: flex;
  align-items: center;
  justify-content: space-between;
}

.span {
  font-size: 10px;
}

.h1 {
  font-size: 16px;
  font-weight: bold;
}

.p {
  font-size: 12px;
  font-weight: lighter;
  display: flex;
  align-items: center;
}
</style>