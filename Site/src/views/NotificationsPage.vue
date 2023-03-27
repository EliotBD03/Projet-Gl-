<template>
  <div class="main">
    <div class="header">
      <MainHeader text="header.notifications"/>
    </div>
    <div class="notifs">
      <MainNotification class="notif" v-for="notif in notifications" :key="notif" :title="notif.title" :time="notif.time" :text="notif.text" :id="notif.id" @delete="deleteNotifications"/>
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
import Swal from "sweetalert2";
export default {
  components: {
    MainNotification,
    MainHeader,
    GoButton
  },
  data() {
    return {
      notifications: [],
      nbr: 1,
      lastPage: 0,
    }
  },
  created() {
    this.getNotifications();
  },
  /*Méthode pour rediriger vers la page d'accueil*/
  methods: {
    async deleteNotifications(id) {
      console.log("test");
      const requestOptions = {
        method: "DELETE",
        headers: {'Authorization': this.$cookies.get("token")},
      }
      fetch('https://babawallet.alwaysdata.net/api/notifications/' + id, requestOptions)
        .then(response => {
          if (!response.ok) {
            const data = response.text();
            if (response.status == 401 && data.trim() === '') {
              throw new Error("Token");
            } else {
              const data = response.json();
              throw new Error(data.error);
            }
          } else {
            Swal.fire('Notification deleted');
          }
        })
        .catch(error => {
          if (error.message === "Token") {
            this.$cookies.remove("token");
            this.$cookies.remove("role");
            Swal.fire('Your connection has expired');
            this.$router.push("/");
          } else {
            GlobalMethods.errorApi(error.message);
          }
        });
    },
    async getNotifications() {
      const requestOptions = {
        method: "GET",
        headers: {'Authorization': this.$cookies.get("token")},
      }
      try {
        const response = await fetch(`https://babawallet.alwaysdata.net/api/notifications/page?page=${this.nbr}&limit=3`, requestOptions);
        if (!response.ok) {
          const data = await response.text();
          if (response.status == 401 && data.trim() === '') {
            throw new Error("Token");
          } else {
            const data = await response.json();
            throw new Error(data.error);
          }
        } else {
          const data = await response.json();
          this.lastPage = data.last_page;
          if (this.lastPage == 0) {
            this.loading = true;
            Swal.fire('No notifications yet !');
          } else {
            this.id = data.id_proposal;
            this.notifications.push(data.notifications);
          }
        }
      } catch (error) {
        if (error.message === "Token") {
          this.$cookies.remove("token");
          this.$cookies.remove("role");
          Swal.fire('Your connection has expired');
          this.$router.push("/");
        } else {
          GlobalMethods.errorApi(error.message);
        }
      }
    },
    redirecting() {
      GlobalMethods.isAClient();
    },
  },
  /*Méthode pour charger la langue sauvegardée en cookie*/
  mounted() {
    if (this.$cookies.get("lang")) {
      this.$i18n.locale = this.$cookies.get("lang");
    } else {
      this.$cookies.set("lang", this.$i18n.locale)
    }
  },
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