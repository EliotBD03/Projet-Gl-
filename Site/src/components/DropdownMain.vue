<template>
  <div class="selectdiv">
    <label>
      <select :value="value" @input="updateValue">
        <option :value="null" disabled> {{ text }} </option>
        <option :value="locale.code" v-for="locale in locales" :key="locale.code">{{ locale.name }}</option>
      </select>
    </label>
  </div>
</template>

<script>
import { getSupportedLocales} from "@/util/i18n/supported-locales";

export default {
  name: "DropdownMain",
  props: ["text","value"],
  data() {
    return {
      /*Liste des langues*/
      locales: getSupportedLocales(),
    }
  },
  /*Methode pour changer la langue*/
  methods: {
    updateValue(event) {
      this.$emit('input', event.target.value)
    }
  }
};
</script>

<style scoped>
body {
  background: #f2f2f2;
}

.selectdiv {
  position: relative;
  /*Don't really need this just for demo styling*/

  float: left;
  min-width: 400px;
  margin: 50px 33%;
}

/* IE11 hide native button (thanks Matt!) */
select::-ms-expand {
  display: none;
}

.selectdiv:after {
  content: '<>';
  font: 17px "Consolas", monospace;
  color: #333;
  -webkit-transform: rotate(90deg);
  -moz-transform: rotate(90deg);
  -ms-transform: rotate(90deg);
  transform: rotate(90deg);
  right: 11px;
  /*Adjust for position however you want*/

  top: 18px;
  padding: 0 0 2px;
  border-bottom: 1px solid #999;
  /*left line */

  position: absolute;
  pointer-events: none;
}

.selectdiv select {
  -webkit-appearance: none;
  -moz-appearance: none;
  appearance: none;
  /* Add some styling */

  display: block;
  width: 100%;
  max-width: 400px;
  height: 50px;
  float: right;
  margin: 5px 0px;
  padding: 0px 24px;
  font-size: 16px;
  line-height: 1.75;
  color: #333;
  background-color: #ffffff;
  background-image: none;
  border: 1px solid #cccccc;
  -ms-word-break: normal;
  word-break: normal;
}
</style>