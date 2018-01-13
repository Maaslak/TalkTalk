#include <iostream>
#include <vector>
#include <fstream>
#include <string>

#include <sys/types.h>
#include <sys/wait.h>
#include <sys/socket.h>
#include <netinet/in.h>
#include <arpa/inet.h>
#include <unistd.h>
#include <netdb.h>
#include <signal.h>
#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <time.h>
#include <pthread.h>


#define SERVER_PORT 1328
#define QUEUE_SIZE 5
#define BUFSIZE 4


pthread_mutex_t mutexio = PTHREAD_MUTEX_INITIALIZER;
pthread_mutex_t mutexsr = PTHREAD_MUTEX_INITIALIZER;

using namespace std;

//struktura zawierająca dane, które zostaną przekazane do wątku


struct klient
{
    bool available;
    bool connected;
    bool talk;
    string login;
    int readsocket;
    int writesocket;
    char flag[1];
    char length_bit[BUFSIZE];
    int length;
    char* message;
    
};

vector <klient> klienci;

vector <string> loginy;

int lenOfMessage(char* bytes){
    int len = 0;
    int base = 1;
    for(int i = 3; i>=0; i--){
            len+= base*(int(bytes[i]));
            base *= 256;
    }
    return len;
}

void lenToMessage(int len, char* message){
    int i = 3;
    while(i >= 0){
        message[i] = len % 256;
        i--;
        len /= 256;
    }
}

void write_mes(struct klient* k1){
    if(k1->flag != "s"){
        write(k1->writesocket,k1->flag,1);
        write(k1->writesocket,k1->length_bit,BUFSIZE);
        write(k1->writesocket,k1->message,k1->length);
    }
}

void write_mes(struct klient* k1,char* mes, int len){
    if(k1->flag == "s"){
        write(k1->readsocket,k1->flag,1);
        char mess_length[BUFSIZE];
        lenToMessage(len, mess_length);
        write(k1->readsocket,mess_length,BUFSIZE);
        write(k1->readsocket,mes,len);
    }
}

void read_mes(struct klient* k1){
    read(k1->readsocket,k1->flag,1);
    read(k1->readsocket,k1->length_bit,BUFSIZE);
    k1->length = lenOfMessage(k1->length_bit);
    char temp[k1->length];
    read(k1->readsocket,temp,k1->length);
    k1->message = &temp[k1->length];
}

void *write_messages(void *k1){
    pthread_detach(pthread_self());
    struct klient *thread_k1 = (struct klient*)k1;
        
    while(thread_k1->talk == true){
        pthread_mutex_lock(&mutexsr);
        //write(thread_k1->writesocket, thread_k1->buffer, BUFSIZE);
        write_mes(thread_k1);
        pthread_mutex_unlock(&mutexsr);
    }
    
    free(thread_k1);
    pthread_exit(NULL);
        
}

void read_messages(struct klient* k1){
    while(k1->talk == true){
        pthread_mutex_lock(&mutexsr);
        read_mes(k1);
        if(k1->flag == "s")
            if(k1->message == "exit")
                k1->talk = false;
        pthread_mutex_unlock(&mutexsr);
        sleep(100);
    }
}

void talk(struct klient* k1){
    k1->talk = true;
    
    pthread_t thread1;
    
    int create_result = pthread_create(&thread1, NULL, write_messages, (void *)k1);
    if (create_result){
       printf("Błąd przy próbie utworzenia wątku, kod błędu: %d\n", create_result);
       exit(-1);
    }
    
    read_messages(k1);
}

void log_in(struct klient* k1){
    do{
        read_mes(k1);
        bool autoryzacja=false;
        
        for(int i=0; i<loginy.size(); i++){
            if(loginy[i]==k1->message){
                autoryzacja=true;
                k1->login=loginy[i];
                k1->writesocket=0;
                k1->available=true;
                k1->connected=false;
                k1->talk = false;
                klienci.push_back(*k1);
                break;
            }
        }
        
        if (autoryzacja){
            k1->available = true;
            char temp[]= "zalogowany";
            write_mes(k1,temp,10);
            cout << "Uzytkownik " << k1->login << " zalogowany" << endl;
        }
        else{
            char temp[] = "bledny login";
            write_mes(k1,temp,12);
            cout << "Niepoprawny login" << endl;
        }
    }while(k1->available == false);
}

void find_somebody_and_call(struct klient* k1){
    while(k1->available == true){
        if(k1->writesocket != 0){
            k1->connected = true;
            talk(k1);
            k1->connected = false;
        }
        //czytanie loginu osoby z ktora chcemy rozmawiac
        
        if(recv(k1->readsocket, k1->length_bit,BUFSIZE,MSG_DONTWAIT)>0){
            k1->connected = true;
            k1->length = lenOfMessage(k1->length_bit);
            char login_adresata[k1->length];
            read(k1->readsocket,login_adresata,k1->length);
            cout << "odczytany login adresata: " << login_adresata << endl;
            
            bool czy_zalogowany=false;
            klient friend1;
            for(int i=0; i<klienci.size(); i++){
            cout << "szukanie adresata" << endl;
                if(klienci[i].login==login_adresata  && klienci[i].available ==true && klienci[i].connected == false){
                    cout << "Polaczono " << k1->login << " i " << klienci[i].login << endl;
                    czy_zalogowany=true;
                    k1->writesocket=klienci[i].readsocket;
                    klienci[i].writesocket=k1->readsocket;
                    k1->connected = true;
                    klienci[i].connected = true;
                    friend1 = klienci[i];
                    break;
                }
            }
            if(czy_zalogowany){
                cout << "Polaczono " << k1->login << " i " << friend1.login << endl;
                char temp[] = "Polaczono!";
                write_mes(k1,temp,10);
                //write_mes(friend1,temp,10);
                talk(k1);
                k1->connected = false;
            }
            else{
                cout << "Nie udalo sie polaczyc" << endl;
                char temp[] = "Nie ma takiego uzytkownika lub jest zajety";
                write_mes(k1,temp,42);
            }
        }
    }
}

void *support_account(void *connection_socket_descriptor){
    
    pthread_detach(pthread_self());
    int *connection_socket_descriptor_th = (int*)connection_socket_descriptor;
    
    struct klient* k1 = (struct klient *) malloc (sizeof(struct klient));
    
    k1->readsocket = *connection_socket_descriptor_th;
    delete(connection_socket_descriptor_th);
    
    log_in(k1);
    
    find_somebody_and_call(k1);
    
    pthread_exit(NULL);
}

int main(int argc, char* argv[])
{
   int server_socket_descriptor;
   int *connection_socket_descriptor;
   int bind_result;
   int listen_result;
   char reuse_addr_val = 1;
   struct sockaddr_in server_address;
   
    fstream plik;
    plik.open( "loginy.txt", ios::in );
    if( plik.good() == true )
    {
        string login;
        while(!plik.eof())
        {
            plik >> login;
            cout << login << endl; //wyświetlenie linii
            loginy.push_back(login);
        }
        //tu operacje na pliku (zapis/odczyt)
        plik.close();
    }
    else{
        cout << "Nie udalo sie otworzyc pliku" << endl;
    }

   //inicjalizacja gniazda serwera
   memset(&server_address, 0, sizeof(struct sockaddr));
   server_address.sin_family = AF_INET;
   server_address.sin_addr.s_addr = htonl(INADDR_ANY);
   server_address.sin_port = htons(SERVER_PORT);

   server_socket_descriptor = socket(AF_INET, SOCK_STREAM, 0);
   if (server_socket_descriptor < 0)
   {
       fprintf(stderr, "%s: Błąd przy próbie utworzenia gniazda..\n", argv[0]);
       exit(1);
   }
   setsockopt(server_socket_descriptor, SOL_SOCKET, SO_REUSEADDR, (char*)&reuse_addr_val, sizeof(reuse_addr_val));

   bind_result = bind(server_socket_descriptor, (struct sockaddr*)&server_address, sizeof(struct sockaddr));
   if (bind_result < 0)
   {
       fprintf(stderr, "%s: Błąd przy próbie dowiązania adresu IP i numeru portu do gniazda.\n", argv[0]);
       exit(1);
   }

   listen_result = listen(server_socket_descriptor, QUEUE_SIZE);
   if (listen_result < 0) {
       fprintf(stderr, "%s: Błąd przy próbie ustawienia wielkości kolejki.\n", argv[0]);
       exit(1);
   }

   while(1)
   {
       connection_socket_descriptor = new int;
       *connection_socket_descriptor = accept(server_socket_descriptor, NULL, NULL);
       if (*connection_socket_descriptor < 0)
       {
           fprintf(stderr, "%s: Błąd przy próbie utworzenia gniazda dla połączenia.\n", argv[0]);
           exit(1);
       }
       
       printf("Hello\n");
       pthread_t thread1;
       int create_result = pthread_create(&thread1, NULL, support_account, (void*)connection_socket_descriptor);
       
        if (create_result){
            printf("Błąd przy próbie utworzenia wątku, kod błędu: %d\n", create_result);
            exit(-1);
        }
   }

   close(server_socket_descriptor);
   return(0);
}

