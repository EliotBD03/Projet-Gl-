new Vue({
  el: "#check",
  data: {
    isChecked: false,
    },
    methods: {
        checked(){          
          this.isChecked = !this.isChecked;
          console.log(this.isChecked);
        }
      }
    });
