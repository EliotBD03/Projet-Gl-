<template>
  <div class="cont">
    <div class="box">
      <input type="text" class="input" placeholder="EAN Code or Provider" v-on:mouseenter="hide=true" v-model="text">
      <i class="fas fa-search"></i>
    </div>
    <div class="containerSearch" v-on:mouseleave="hide=false">
      <div class="search" v-for="element in filterFunction()" :key="element" v-show="hide">
        <p>{{ element.name }} {{ element.EAN }}</p>
      </div>
      <div class="search" v-if="filterFunction().length === 0" v-show="hide">
        <p>No results</p>
    </div>
  </div>
</div>
</template>

<script>

export default {
  name: "SearchBar",
  methods: {
    filterFunction() {
      return this.elements.filter((element) => {
        return element.EAN.includes(this.text) || element.name.includes(this.text);
      });
    },
  },
  data() {
    return {
      hide: false,
      text: "",
      elements: [
        {
          id: 1,
          name: "Dupuis",
          EAN: "1234567890123",
          date: "01/01/2020",
        },
        {
          id: 2,
          name: "Bourgies",
          EAN: "2356789012345",
          date: "02/02/2020",
        },
        {
          id: 3,
          name: "D'Haene",
          EAN: "3456789012345",
          date: "03/03/2020",
        },
        {
          id: 1,
          name: "Dupuis",
          EAN: "1234567890123",
          date: "01/01/2020",
        },
        {
          id: 2,
          name: "Bourgies",
          EAN: "2356789012345",
          date: "02/02/2020",
        },
        {
          id: 3,
          name: "D'Haene",
          EAN: "3456789012345",
          date: "03/03/2020",
        },
        {
          id: 1,
          name: "Dupuis",
          EAN: "1234567890123",
          date: "01/01/2020",
        },
        {
          id: 2,
          name: "Bourgies",
          EAN: "2356789012345",
          date: "02/02/2020",
        },
        {
          id: 3,
          name: "D'Haene",
          EAN: "3456789012345",
          date: "03/03/2020",
        },
      ],
    };
  },
  computed: function() {
    this.filterFunction();
  }
};

</script>

<style scoped>
.cont{
  display: flex;
  justify-content: center;
  align-items: center;
  flex-direction: column;
  align-content: center;
  background-color: #ffffff;
}

.containerSearch {
  width: 720px;
  height: 500px;
  overflow-y: scroll;
  color: gray;
}
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
  display: flex;
  align-items: center;
  justify-content: center;
  flex-direction: column;
}

.search:hover {
  cursor: pointer;
}

.search {
  padding: 2px;
  margin: 8px;
  border: 1px solid #B1B9FC;
  border-radius: 8px;
}

.input {
  padding: 10px;
  width: 80px;
  height: 80px;
  border: 4px solid #3348f3;
  border-radius: 50px;
  box-sizing: border-box;
  font-size: 26px;
  color: transparent;
  outline: none;
  transition: .5s;
  background: url("@/assets/search-icon.png") no-repeat center center;
  background-size: 45px;
  margin-bottom: 15px;
}
.input:focus::placeholder{
  color: transparent;
}

.box:hover input{
  width: 720px;
  background: #ffffff;
  border-radius: 10px;
  color: grey;
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