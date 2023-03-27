<template>
  <div class="card">
    <div class="img"></div>
    <div class="textBox">
      <div class="textContent">
        <div class="h1">{{ $t("notifications.title",{name: title}) }}</div>
        <span class="span">{{ $t("notifications.time",{time: time}) }}</span>
      </div>
      <div class="p">{{  $t("notifications.text",{text: text}) }}</div>
      <div>
      </div>
    </div>
    <div class="statesButtons">
      <div class="acceptbutton" @click.prevent.left="accept()" v-show="checkStatus()">
      ACCEPT
      </div>
      <div class="refusebutton" @click.prevent.left="refuse()" v-show="checkStatus()">
        REFUSE
      </div>
      <div class="seenbutton" @click.prevent.left="deleted(id)" v-show="checkStatus()">
        DELETE
        </div>
    </div>
  </div>
</template>

<script>
export default {
  name: "MainNotification",
  props: ["title","time","text","color","id"],
  methods: {
    accept() {
      console.log("accept");
    },
    refuse() {
      console.log("refuse");
    },
    deleted(id) {
      this.$emit("delete", id);
    },
    checkStatus() {
    }
  }
}
</script>

<style scoped>
.card {
  width: 700px;
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

.statesButtons {
  display: flex;
  align-items: center;
  justify-content: center;
  flex-direction: column;
  margin: 20px;
}

.acceptbutton {
  width: 50px;
  height: 20px;
  background: #34c98e;
  border-radius: 10px;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 10px;
  color: white;
  margin-bottom: 10px;
}

.refusebutton {
  width: 50px;
  height: 20px;
  background: red;
  border-radius: 10px;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 10px;
  color: white;
}
</style>