<template>
    <div class="main">
        <div class="header">
            <MainHeader text="header.notifications"/>
        </div>
        <div class="notifs">
            <MainNotification class="notif" v-for="notif in notifications" :key="notif" :time="notif.creationDate" :text="notif.context" :id="notif.contractId" :id_notification="notif.notificationId" @delete="deleteNotifications" @accept="acceptNotification" @refuse="refuseNotification"/>
        </div>
        <div class="bottombuttons">
            <div class="homebutton" @click.prevent.left="redirecting()">
                <GoButton text="header.home" :colore="'#B1B9FC'"/>
            </div>
            <div class="refresh-button" @click="refreshNotifications">
                <GoButton text="header.refresh" :colore="'#B1B9FC'"/>
            </div>
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
            timer: null,
        }
    },
    created() {
        this.getNotifications();
        this.timer = setInterval(() => {
            this.refreshNotifications();
        }, 5 * 60 * 1000);
    },
    beforeDestroy() {
        clearInterval(this.timer);
    },
    /*Méthode pour rediriger vers la page d'accueil*/
    methods: {
        async refreshNotifications(){
            await this.getNotifications();
        },
        async deleteNotifications(id_notification) {
            console.log("test");
            const requestOptions = {
                method: "DELETE",
                headers: {'Authorization': this.$cookies.get("token")},
            }
            fetch('https://babawallet.alwaysdata.net/api/common/notifications/' + id_notification, requestOptions)
                .then(response => {
                    if (!response.ok) {
                        if (response.status == 401) {
                            throw new Error("Token");
                        } else {
                            const data = response.json();
                            throw new Error(data.error);
                        }
                    } else {
                        Swal.fire('Notification deleted');
                        this.refreshNotifications();
                    }
                })
                .catch(error => {
                    if (error.message === "Token") {
                        GlobalMethods.errorToken();
                    } else {
                        GlobalMethods.errorApi(error.message);
                    }
                });
            await this.refreshNotifications();
        },
        async getNotifications() {
            const requestOptions = {
                method: "GET",
                headers: {'Authorization': this.$cookies.get("token")},
            }
            try {
                const response = await fetch(`https://babawallet.alwaysdata.net/api/common/notifications/page?page=${this.nbr}&limit=3`, requestOptions);
                if (!response.ok) {
                    if (response.status == 401) {
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
                        this.notifications = data.allNotifications;
                        this.notifications = this.notifications.flat();
                    }
                }
            } catch (error) {
                if (error.message === "Token") {
                    GlobalMethods.errorToken();
                } else {
                    GlobalMethods.errorApi(error.message);
                }
            }
        },
        async acceptNotification(id_notification) {
            const requestOptions = {
                method: "POST",
                headers: {'Authorization': this.$cookies.get("token")},
            }
            fetch("https://babawallet.alwaysdata.net/api/common/notifications/accept_notification/" + id_notification, requestOptions)
                .then(response => {
                    if (!response.ok) {
                        if (response.status == 401) {
                            throw new Error("Token");
                        } else {
                            const data = response.json();
                            throw new Error(data.error);
                        }
                    } else {
                        Swal.fire('Notification accepted');
                        this.refreshNotifications();
                    }
                })
                .catch(error => {
                    if (error.message === "Token") {
                        GlobalMethods.errorToken();
                    } else {
                        GlobalMethods.errorApi(error.message);
                    }
                });
            await this.refreshNotifications();
        },
        async refuseNotification(id_notification) {
            const requestOptions = {
                method: "POST",
                headers: {'Authorization': this.$cookies.get("token")},
            }
            fetch("https://babawallet.alwaysdata.net/api/common/notifications/refuse_notification/" + id_notification, requestOptions)
                .then(response => {
                    if (!response.ok) {
                        if (response.status == 401) {
                            throw new Error("Token");
                        } else {
                            const data = response.json();
                            throw new Error(data.error);
                        }
                    } else {
                        Swal.fire('Notification refused');
                        this.refreshNotifications();
                    }
                })
                .catch(error => {
                    if (error.message === "Token") {
                        GlobalMethods.errorToken();
                    } else {
                        GlobalMethods.errorApi(error.message);
                    }
                });
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
    z-index: 9999;
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

.bottombuttons {
    display: flex;
    justify-content: space-evenly;
    align-items: center;
    margin: 10px;
}
</style>