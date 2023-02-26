<template>
  <div class="box">
    <form name="search">
      <input type="text" class="input" name="txt" onmouseout="this.value = ''; this.blur();" placeholder="EAN Code or Provider">
    </form>
    <i class="fas fa-search"></i>

  </div>
</template>
<script>
export default {
  name: "SearchBar",
}
</script>

<style scoped>
body{
  margin: 0;
  padding: 0;
  background: #19161c;
  height: 100vh;
  display: flex;
  justify-content: space-evenly;
  align-items: center;
  flex-direction: column;
  align-content: center;
}
.box{
  position: relative;
}

.input {
  padding: 10px;
  width: 80px;
  height: 80px;
  border: 4px solid #3348f3;
  border-radius: 50px;
  box-sizing: border-box;
  font-size: 26px;
  color: #3348f3;
  outline: none;
  transition: .5s;
  background: url("@/assets/search-icon.png") no-repeat center center;
  background-size: 45px;
}
.input:focus::placeholder{
  color: transparent;
}

.box:hover input{
  width: 350px;
  background: #ffffff;
  border-radius: 10px;
}
.box i{
  position: absolute;
  top: 50%;
  right: 15px;
  transform: translate(-50%,-50%);
  font-size: 26px;
  color: #3348f3;
  transition: .2s;
}
.box:hover i{
  opacity: 0;
  z-index: -1;
}
</style>