<template>
  <div class="main">
    <div class="header">
      <MainHeader text="Notifications"/>
    </div>
    <div class="notifs">
      <MainNotification class="notif" v-for="notif in notifications" :key="notif" :title="notif.title" :time="notif.time" :text="notif.text" />
    </div>
    <div class="homebutton" @click.prevent.left="$router.push('{name: HomeClient}')">
      <GoButton text="Home" :colore="'#B1B9FC'"/>
    </div>
  </div>
</template>

<script>
import MainHeader from "@/components/MainHeader.vue";
import MainNotification from "@/components/MainNotification.vue";
import GoButton from "@/components/GoButton.vue";
export default {
  components: {
    MainNotification,
    MainHeader,
    GoButton
  },
  data () {
    return {
      notifications: [
        {
          title: "Maxime",
          time: "12",
          text: "has updated his contract"
        },
        {
          title: "Claire",
          time: "25",
          text: "has accepted your proposal"
        },
        {
          title: "Adrien",
          time: "35",
          text: "has closed the contract"
        },
        {
          title: "Jean",
          time: "45",
          text: "has updated his contract"
        },
        {
          title: "Kiefer",
          time: "55",
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