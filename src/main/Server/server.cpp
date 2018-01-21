#include <iostream>
#include <vector>
#include <fstream>
#include <string>
//#include <conio.h>

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


#define SERVER_PORT 1306
#define QUEUE_SIZE 5
#define BUFSIZE 4


//pthread_mutex_t mutexio = PTHREAD_MUTEX_INITIALIZER;
pthread_mutex_t mutexsr = PTHREAD_MUTEX_INITIALIZER;

using namespace std;

//struktura zawierająca dane, które zostaną przekazane do wątku


struct klient
{
    bool available;
    bool connected;
    bool talk;
    string login;
    string talk_friend;
    int readsocket;
    int writesocket;
    char flag;
    char length_bit[BUFSIZE];
    unsigned int length;
    char* message;
    
};

vector <klient> klienci;

vector <string> loginy;

unsigned int lenOfMessage(char* bytes){
    unsigned int len = 0;
    int base = 1;
    for(int i = 3; i>=0; i--){
            //len+= base*(unsigned int)(bytes[i]);
            if((int)(bytes[i])<0){
                len+=base * ((int)bytes[i] + 256);
            }
            else
                len+=base * (int)bytes[i];
            base *= 256;
    }
    return len;
}

void lenToMessage(unsigned int len, char* message){
    int i = 3;
    while(i >= 0){
        message[i] = len % 256;
        i--;
        len /= 256;
    }
}

void write_mes(struct klient* k1){
    //if(k1->flag != 's'){
        write(k1->writesocket,&(k1->flag),1);
        write(k1->writesocket,k1->length_bit,BUFSIZE);
        write(k1->writesocket,k1->message,k1->length);
    //}
}

void write_mes(struct klient* k1,char* mes, int len){
    //if(k1->flag == 's'){
        write(k1->readsocket,&(k1->flag),1);
        char mess_length[BUFSIZE];
        lenToMessage(len, mess_length);
        write(k1->readsocket,mess_length,BUFSIZE);
        write(k1->readsocket,mes,len);
    //}
}

void read_mes(struct klient* k1){
    read(k1->readsocket,&(k1->flag),1);
    read(k1->readsocket,k1->length_bit,BUFSIZE);
    k1->length = lenOfMessage(k1->length_bit);
    //printf("%u\n",k1->length);
    k1->message = new char[k1->length];
    unsigned int temp_len = k1->length;
    unsigned int counter = 0;
    do{
        if(temp_len>100){
            if(read(k1->readsocket,k1->message+counter,100)<0){
                printf("nie odczytal\n");
                exit(1);
            }
            counter=counter+100;
            temp_len = temp_len-100;
            //printf("pobral 100\n");
        }
        else{
            if(read(k1->readsocket,k1->message+counter,temp_len)<0){
                printf("nie odczytal\n");
                exit(1);
            }
            counter+=temp_len;
            temp_len=0;
            //printf("pobral koniec\n");
        }
    }while(temp_len!=0);
    printf("%d\n\n",counter);
    
}

void read_mes(struct klient* k1, char flag){
    k1->flag = flag;
    read(k1->readsocket,k1->length_bit,BUFSIZE);
    k1->length = lenOfMessage(k1->length_bit);
    k1->message = new char[k1->length];
    read(k1->readsocket,k1->message,k1->length);
}

void talk_one_thread_new(struct klient* k1){
    printf("Rozmowa\n");
    k1->talk = true;
    while(k1->talk == true){
        printf("talk\n");
        read(k1->readsocket,&(k1->flag),1);
        printf("odczytano\n");
        
        write(k1->writesocket,&(k1->flag),1);
        read(k1->readsocket,k1->length_bit,BUFSIZE);
        printf("odczytano2\n");
        write(k1->writesocket,k1->length_bit,BUFSIZE);
        k1->length = lenOfMessage(k1->length_bit);
        //printf("%d\n",k1->length);
        //printf("%u\n",k1->length);
        //k1->message = new char[100];
        
        char message;
        printf("%d\n",k1->length);
        while(read(k1->readsocket,&message,1)){
                write(k1->writesocket,&message,1);
            }
        
        if(k1->flag == 's')
             if(k1->message == "exit"){
                 k1->talk = false;
                 break;
             } 
        
        
        
        
        /*int counter = k1->length;
        do{
            if(counter>100){
                k1->message = new char[100];
                if(read(k1->readsocket,k1->message,100)<=0)
                    printf("error");
                if(write(k1->writesocket,k1->message,100)<=0)
                    printf("error");
                counter = counter - 100;
                delete(k1->message);
            }
            else if(counter>0){
                //delete(k1->message);
                k1->message = new char[counter];
                printf("%d\n",counter);
                if(read(k1->readsocket,k1->message,counter)<=0)
                    printf("error");
                if(write(k1->writesocket,k1->message,counter)<=0)
                    printf("error");
                counter=0;
                delete(k1->message);
                break;
            }       
        }while(counter>0);
        //delete(k1->message);*/
    }
}

void talk_one_thread(struct klient* k1){
    printf("Rozmowa\n");
    k1->talk = true;
    while(k1->talk = true){
        read_mes(k1);
        if(k1->flag == 's')
             if(k1->message == "exit")
                k1->talk = false;
        write_mes(k1);
        delete(k1->message);
    }
    k1->talk = false;
}

void log_in(struct klient* k1){
    do{
        if(read(k1->readsocket,&(k1->flag),1)){
            read_mes(k1,k1->flag);
        bool autoryzacja=false;
        
        bool free_account = true;
        
        for(int i=0; i<klienci.size(); i++)
            if(klienci[i].login == k1->message){
                free_account = false;
                break;
            }
        
        if(free_account){
            for(int i=0; i<loginy.size(); i++){
                if(loginy[i]==k1->message){
                    autoryzacja=true;
                    k1->login=loginy[i];
                    k1->writesocket=0;
                    k1->connected=false;
                    k1->talk = false;
                    klienci.push_back(*k1);
                    break;
                }
            }
        }
        
        if (autoryzacja){
            k1->available = true;
            char temp[]= "ok";
            write_mes(k1,temp,2);
            cout << "Uzytkownik " << k1->login << " zalogowany" << endl;
            
            
            string name = k1->login+".txt";
            const char * file_name = name.c_str();
            ifstream ifs(file_name);
            string content( (istreambuf_iterator<char>(ifs) ),
            (istreambuf_iterator<char>()    ) );
            
            char logins[sizeof(content)];
            strncpy(logins, content.c_str(),sizeof(content));
            
            write_mes(k1,logins,sizeof(content)-1);
            delete(k1->message);
        }
        else{
            k1-> flag = 's';
            char temp[] = "bledny login";
            write_mes(k1,temp,12);
            cout << "Niepoprawny login: " << k1->message << endl;
            delete(k1->message);
        }
        }
        k1->flag = 'n';
        
    }while(k1->available == false);
}

void find_somebody_and_call(struct klient* k1){
    while(k1->available == true){
        k1->connected = false;
        if(k1->writesocket != 0){
            k1->connected = true;
            /*const char * name = k1->talk_friend.c_str();
            char* file_name = (char*)name;
            
            int len = lenOfMessage(file_name);
            k1->flag = 't';
            write_mes(k1,file_name,len);
            read(k1->readsocket,&(k1->flag),1);
            read_mes(k1,k1->flag);
            
            if(k1->flag == 'k')*/
                talk_one_thread(k1);//_new(k1);
            /*else{
                k1->connected = false;
                k1->writesocket = 0;
            }*/
            
        }
        //czytanie loginu osoby z ktora chcemy rozmawiac
        char flag;
        if(recv(k1->readsocket, &flag,1,MSG_DONTWAIT)>0){
            //k1->connected = true;
            if(flag == 'c'){
                k1->connected = true;
                read_mes(k1, flag);
                
                cout << "dodano kontakty uzytkownika " << k1->login << endl;
                
                fstream plik;
                string name = k1->login+".txt";
                const char * file_name = name.c_str();
                
                plik.open( file_name , ios::out );
                if( plik.good() == true )
                {
                    plik << k1->message;
                    cout << k1->message << endl; //wyświetlenie linii
                    plik.close();
                }
                else{
                    cout << "Nie udalo sie otworzyc pliku" << endl;
                    exit(1);
                }
                
            }
            else if (flag == 's') {
                read_mes(k1, flag);
                char login_adresata[k1->length];
                strcpy(login_adresata, k1->message);
                cout << "odczytany login adresata: " << login_adresata << endl;
            
                bool czy_dostepny=false;
                //klient friend1;
                for(int i=0; i<klienci.size(); i++){
                cout << "szukanie adresata" << endl;
                    if(klienci[i].login==login_adresata/*  && klienci[i].available ==true && klienci[i].connected == false*/){
                        k1->connected = true;
                        czy_dostepny=true;
                        k1->writesocket=klienci[i].readsocket;
                        klienci[i].writesocket=k1->readsocket;
                        k1->connected = true;
                        klienci[i].connected = true;
                        k1->talk_friend = klienci[i].login;
                        klienci[i].talk_friend=k1->login;
                        delete(k1->message);
                        break;
                    }
                }
                if(czy_dostepny){
                    cout << "Polaczono " << k1->login << " i " << k1->talk_friend << endl;
                    char temp[] = "Polaczono!";
                    write_mes(k1,temp,10);
                    //char temp1[] = k1->login;
                    //int len = k1->login;
                    //write_mes(friend1,temp,10);
                    talk_one_thread(k1);
                    k1->connected = false;
                }
                else{
                    cout << "Nie udalo sie polaczyc" << endl;
                    char temp[] = "Nie ma takiego uzytkownika lub jest zajety";
                    k1->flag = 's';
                    write_mes(k1,temp,42);
                }
            }
            else if(flag == 'e'){
                read_mes(k1, flag);
                k1->connected = true;
                
                k1->available = false;
                
                fstream plik;
                string name = k1->login+".txt";
                const char * file_name = name.c_str();
                
                plik.open( file_name , ios::in );
                if( plik.good() == true )
                {
                    plik << k1->message;
                    cout << k1->message << endl; //wyświetlenie linii
                    plik.close();
                }
                else{
                    cout << "Nie udalo sie otworzyc pliku" << endl;
                    exit(1);
                }
            }
            
        }
    }
    //delete(k1);
}

void *support_account(void *connection_socket_descriptor){
    
    pthread_detach(pthread_self());
    /*int *connection_socket_descriptor_th = (int*)connection_socket_descriptor;
    
    struct klient* k1 = (struct klient *) malloc (sizeof(struct klient));
    
    k1->readsocket = *connection_socket_descriptor_th;
    delete(connection_socket_descriptor_th);*/
    
    int *read_socket = (int*)connection_socket_descriptor;
    
    struct klient* k1 = (struct klient *) malloc (sizeof(struct klient));
    
    k1->readsocket = *read_socket;
    delete(read_socket);
    
    log_in(k1);
    
    find_somebody_and_call(k1);
    
    for(int i=0; i < klienci.size() ; i++){
        if(klienci[i].login == k1->login){
            klienci.erase(i+klienci.begin());
            delete(k1);
        }    
    }
    
    cout << klienci.size() << endl;
    
    cout << "wylaczenie watku klienta" <<endl;
    pthread_exit(NULL);
}

void *switch_of(void *on){
    while(1){
        char a;
        cin >> a;
        if(a == 'e'){
            on = 0;
            cout << "serwer wylaczony" << endl;
            exit(1); 
        }
        else if(a == 'c'){
            if(klienci.size() == 0)
                cout << "0" << endl;
            else
                for(int i=0; i<klienci.size(); i++){
                    cout << klienci[i].login << endl;
                }
        }
        else if(a == 's'){
            cout << klienci.size() << endl;
        }
    }
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
        exit(1);
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
   
   bool* on = new bool;
   *on = true;
   
   pthread_t thread;
       pthread_create(&thread, NULL, switch_of, (void*)on);

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

/*void *write_messages(void *k1){
    pthread_detach(pthread_self());
    struct klient *thread_k1 = (struct klient*)k1;
        
    while(thread_k1->talk == true){
        //pthread_mutex_lock(&mutexsr);
        //write(thread_k1->writesocket, thread_k1->buffer, BUFSIZE);
        read_mes(thread_k1);
        if(thread_k1->flag == 's')
             if(thread_k1->message == "exit")
                thread_k1->talk = false;
        write_mes(thread_k1);
        delete(thread_k1->message);
        //pthread_mutex_unlock(&mutexsr);
        //sleep(10);
    }
    
    free(thread_k1);
    pthread_exit(NULL);
        
}

void read_messages(struct klient* k1){
    while(k1->talk == true){
    
        pthread_mutex_lock(&mutexsr);
        read_mes(k1);
        if(k1->flag == 's')
            if(k1->message == "exit")
                k1->talk = false;
        delete(k1->message);
        pthread_mutex_unlock(&mutexsr);
        sleep(10);
        
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
}*/
