<template>
  <div class="contact-form">
    <form>
      <label for="name">Name :</label>
      <input type="text" required="">
      <label for="email">Adress :</label>
      <input type="email" id="email" name="email" required="">
      <GoButton text="Add"/>
    </form>
  </div>

</template>
<script>
import GoButton from "@/components/GoButton.vue";

export default {
  name: "ContactForm",
  components: {GoButton}
}
</script>

<style scoped>
.contact-form {
  background-color: #f2f2f2;
  padding: 50px;
  border-radius: 10px;
}

.contact-form label {
  color: black;
}


.contact-form form {
  display: flex;
  flex-direction: column;
}

.contact-form label {
  margin-bottom: 10px;
}

textarea {
  resize: none;
  height: 80px;
  width: 200px;
}

.contact-form input, .contact-form textarea {
  padding: 10px;
  border: none;
  border-radius: 5px;
  margin-bottom: 20px;
}

.contact-form input:focus, .contact-form textarea:focus {
  outline: none;
  box-shadow: 0 0 5px #B1B9FC;
  transform: scale(1.05);
  transition: transform 0.3s ease-in-out;
}

</style>