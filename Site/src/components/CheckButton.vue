<template>
  <div>
    <div>
      <input type="checkbox" :id="name" :value="name" @click.prevent.left="$emit('event')">
      <!--Pour Maxime : @click ici est une solution pour GoButton-->
      <label :for="name">{{ name }}</label>
    </div>
  </div>
</template>

<script>
export default {
  name: "checkButton",
  props: ["name"]
}
</script>