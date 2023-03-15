<template>
  <div class="main">
    <div class="header">
      <MainHeader text="header.notifications"/>
    </div>
    <div class="notifs">
      <MainNotification class="notif" v-for="notif in notifications" :key="notif" :title="notif.title" :time="notif.time" :text="notif.text" />
    </div>
    <div class="homebutton" @click.prevent.left="redirecting()">
      <GoButton text="header.home" :colore="'#B1B9FC'"/>
    </div>
  </div>
</template>

<script>
import MainHeader from "@/components/MainHeader.vue";
import MainNotification from "@/components/MainNotification.vue";
import GoButton from "@/components/GoButton.vue";
import GlobalMethods from "@/components/GlobalMethods.vue";
export default {
  components: {
    MainNotification,
    MainHeader,
    GoButton
  },
  /*Méthode pour rediriger vers la page d'accueil*/
  methods: {
    redirecting() {
      GlobalMethods.isAClient(this.$cookies.get("role"));
    }
  },
  /*Méthode pour charger la langue sauvegardée en cookie*/
  mounted() {
    if (this.$cookies.get("lang")) {
      this.$i18n.locale = this.$cookies.get("lang");
    } else {
      this.$cookies.set("lang", this.$i18n.locale)
    }
  },
  data () {
    return {
      notifications: [
        {
          title: "Maxime",
          time: "12 minutes",
          text: "has updated his contract"
        },
        {
          title: "Claire",
          time: "1 hour",
          text: "has accepted your proposal"
        },
        {
          title: "Adrien",
          time: "35 seconds",
          text: "has closed the contract"
        },
        {
          title: "Jean",
          time: "45 minutes",
          text: "has updated his contract"
        },
        {
          title: "Kiefer",
          time: "2 days",
          text: "has accepted your proposal"
        }
      ]
    }
  }
};
</script>

<style scoped>

.main {
  display: flex;
  flex-direction: column;
  justify-content: space-evenly;
  height: 100vh;
}

.header {
  display: flex;
  align-items: center;
  justify-content: center;
}

.notifs {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: space-evenly;
  overflow-y: scroll;
}

.notif {
  margin: 5px;
}
</style>