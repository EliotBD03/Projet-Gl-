<template>
  <div class="button" @click.prevent.left="choose(expr)">
    <button>{{ text }}</button>
  </div>
</template>

<script>
export default {
  name: "GoButton",
  props: ["text","color","redirect","expr"],
  methods: {
    handler() {
      this.$router.push(this.redirect);
    },
    choose(expr) {
      switch (expr) {
        case "change":
          this.handler();
          break;
        case "test":
          console.log("test");
          break;
      }
    },
    data() {
      return {
        color: this.color,
      };
    },
  }
}
</script>

<style scoped>
button {
  padding: 15px 100px;
  border: unset;
  border-radius: 15px;
  color: #212121;
  z-index: 1;
  background: #e8e8e8;
  position: relative;
  font-weight: 1000;
  font-size: 17px;
  -webkit-box-shadow: 4px 8px 19px -3px rgba(0,0,0,0.27);
  box-shadow: 4px 8px 19px -3px rgba(0,0,0,0.27);
  transition: all 250ms;
  overflow: hidden;
  cursor: pointer;
}

button::before {
  content: "";
  position: absolute;
  top: 0;
  left: 0;
  height: 100%;
  width: 0;
  background-color: v-bind(color);
  border-radius: 15px;
  z-index: -1;
  -webkit-box-shadow: 4px 8px 19px -3px rgba(0,0,0,0.27);
  box-shadow: 4px 8px 19px -3px rgba(0,0,0,0.27);
  transition: all 250ms
}

button:hover {
  color: #e8e8e8;
}

button:hover::before {
  width: 100%;
}
</style>