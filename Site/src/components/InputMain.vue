<template>
  <input class="input" :value="value" @input="updateValue" :placeholder=text />
</template>

<script>
export default {
  name: "InputMain",
  props: ["value", "text"],
  methods: {
    updateValue(event) {
      this.$emit('input', event.target.value)
    }
  }
}
</script>

<style scoped>
.input {
  font-weight: 500;
  font-size: 14px;
  height: 50px;
  width: 400px;
  border-radius: 10px;
  padding: 0 24px;
  border: none;
  border-bottom: 1px solid #e5e5e5;
  outline: none;
  display: flex;
  margin: 10px;
}

.input:focus {
  border-bottom: 1px solid #B1B9FC;
  -webkit-transition: 0.1s;
  transition: 0.5s;
}
</style>